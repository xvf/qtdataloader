package com.qt.jobs;

import com.qt.domain.ClinicItem;
import com.qt.domain.MatchedClinic;
import com.qt.listener.JobCompletionNotificationListener;
import com.qt.processor.MatchQuartetProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import javax.sql.DataSource;

/**
 * Created by asrivastava on 5/9/17.
 */
@Configuration("MatchQuartetClinicsJob")
public class MatchQuartetClinicsJob {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    public StepBuilderFactory stepBuilderFactory;
    @Autowired
    public MatchQuartetProcessor processor;
    @Autowired
    public DataSource mainDataSource;
    @Autowired
    JobCompletionNotificationListener listener;

    /**
     * The steps for this job
     *
     * 1. Fetch rows from NYC Data where active =0
     * 2. For each for these rows, check if row exists in quartet data source {@link MatchQuartetProcessor}
     * 3. If so, create an id, active_flag tuple of {@link MatchedClinic}
     * 4. For each of this hash_id, update the active_flag
     * @param step1
     * @return
     */
    @Bean("matchQuartetClinicsJob")
    public Job matchQuartetClinicsJob(@Qualifier("matchQuartetClinicsJobStep1") Step step1) {
        Job matchQuartetClinicsJob = jobBuilderFactory.get("matchQuartetClinicsJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step1)
                .end()
                .build();
        return matchQuartetClinicsJob;
    }

    @Bean
    public Step matchQuartetClinicsJobStep1() {
        return stepBuilderFactory.get("matchQuartetClinicsJobStep1")
                .<ClinicItem, MatchedClinic> chunk(10)
                .reader(matchQuartetClinicsJobReader())
                .processor(processor)
                .writer(matchQuartetClinicsJobWriter())
                .build();
    }

    @Bean
    public JdbcCursorItemReader<ClinicItem> matchQuartetClinicsJobReader() {
        JdbcCursorItemReader<ClinicItem> reader = new JdbcCursorItemReader<ClinicItem>();
        reader.setDataSource(mainDataSource);
        reader.setSql("SELECT hash_id, name_1, name_2, zip, street_address from NYC_MASTER_CLINIC_DATA WHERE active is NULL;");
        reader.setRowMapper(new BeanPropertyRowMapper<>(ClinicItem.class));
        return reader;
    }

    @Bean
    public JdbcBatchItemWriter<MatchedClinic> matchQuartetClinicsJobWriter() {
        JdbcBatchItemWriter<MatchedClinic> writer = new JdbcBatchItemWriter<MatchedClinic>();
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<MatchedClinic>());
        writer.setDataSource(mainDataSource);
        writer.setAssertUpdates(false);
        writer.setSql("UPDATE NYC_MASTER_CLINIC_DATA SET active= :active WHERE hash_id= :hash_id AND :active = 1");
        return writer;
    }
}
