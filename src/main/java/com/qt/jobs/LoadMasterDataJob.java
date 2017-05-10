package com.qt.jobs;

import com.qt.configuration.DataSourceConfiguration;
import com.qt.domain.ClinicItem;
import com.qt.listener.JobCompletionNotificationListener;
import com.qt.processor.ClinicItemProcessor;
import com.qt.reader.ClinicStreamReader;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.support.MapJobRegistry;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.sql.DataSource;

/**
 * Created by asrivastava on 5/9/17.
 */
@Configuration
public class LoadMasterDataJob{
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    public StepBuilderFactory stepBuilderFactory;
    @Autowired
    public ClinicStreamReader reader;
    @Autowired
    public DataSource mainDataSource;
    @Autowired
    JobCompletionNotificationListener listener;
    @Autowired
    public ClinicItemProcessor processor;

    @Bean("importMasterClinicData")
    public Job importMasterClinicData(@Qualifier("importMasterClinicDataStep1") Step importMasterClinicDataStep1) {
        Job importMasterClinicdata = jobBuilderFactory.get("importMasterClinicData")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(importMasterClinicDataStep1)
                .end()
                .build();
        return importMasterClinicdata;
    }

    @Bean
    public Step importMasterClinicDataStep1() {
        return stepBuilderFactory.get("importMasterClinicDataStep1")
                .<ClinicItem, ClinicItem> chunk(10)
                .reader(reader)
                .processor(processor)
                .writer(importMasterClinicDataWriter())
                .build();
    }

    /**
     * Writer writes the data to the MYSQL NYCMaster database. It only inserts if the hash id is not present in the
     * table.
     * @return
     */
    @Bean
    public JdbcBatchItemWriter<ClinicItem> importMasterClinicDataWriter() {
        JdbcBatchItemWriter<ClinicItem> writer = new JdbcBatchItemWriter<ClinicItem>();
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<ClinicItem>());
        writer.setAssertUpdates(false);
        writer.setSql("INSERT IGNORE INTO NYC_MASTER_CLINIC_DATA (hash_id, name_1,name_2,city,latitude,longitude, " +
                "zip,street_address,phone)" +
                " values (:hash_id, :name_1, :name_2, :city,:latitude,:longitude," +
                " :zip,:street_address,:phone)");
        writer.setDataSource(mainDataSource);

        return writer;
    }
}
