package com.qt.configuration;

import com.qt.jobs.JobLauncherDetails;
import org.quartz.Trigger;
import org.quartz.spi.JobFactory;
import org.springframework.batch.core.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by asrivastava on 5/9/17.
 */

/**
 * This class is responsible for creating the Quartz Scheduler Beans
 * Will only be on if quartz.enabled is true
 */
@Configuration
@ConditionalOnProperty(name = "quartz.enabled")
public class SchedulerConfig {

    @Autowired
    public Job importMasterClinicData;
    @Autowired
    public Job matchQuartetClinicsJob;

    @Bean
    public JobFactory jobFactory(ApplicationContext applicationContext)
    {
        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }

    /**
     * Scheduler factory bean for loadMasterJobTrigger
     * @param jobFactory
     * @param loadMasterJobTrigger
     * @return
     * @throws IOException
     */
    @Bean
    @Autowired
    public SchedulerFactoryBean schedulerFactoryBeanLoadData( JobFactory jobFactory,
                                                     @Qualifier("loadMasterJobTrigger") Trigger loadMasterJobTrigger) throws IOException {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setJobFactory(jobFactory);
        factory.setTriggers(new Trigger[]{loadMasterJobTrigger});
        return factory;
    }

    /**
     * Scheduler factory bean for matchQuartetClinicsJobTrigger
     * @param jobFactory
     * @param matchQuartetClinicsJobTrigger
     * @return
     * @throws IOException
     */
    @Bean
    @Autowired
    public SchedulerFactoryBean schedulerFactoryBeanQuartetData( JobFactory jobFactory,
                                                      @Qualifier("matchQuartetClinicsJobTrigger") Trigger matchQuartetClinicsJobTrigger) throws IOException {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setJobFactory(jobFactory);
        factory.setTriggers(new Trigger[]{matchQuartetClinicsJobTrigger});
        return factory;
    }

    /**
     * CronTriggerFactoryBean for loadDataCronTrigger
     * @param jobDetail
     * @param cronExpression
     * @return
     */
    @Bean("loadMasterJobTrigger")
    public CronTriggerFactoryBean loadDataCronTriggerBean(@Qualifier("loadMasterJobDetail") JobDetailBean jobDetail,
                                                          @Value("${loadMaster.cron}") String cronExpression) {
        return createCronTrigger(jobDetail, cronExpression);
    }

    /**
     * CronTriggerFactoryBean for matchQuartetClinicsJob
     * @param jobDetail
     * @param cronExpression
     * @return
     */
    @Bean("matchQuartetClinicsJobTrigger")
    public CronTriggerFactoryBean matchQuartetClinicsJobTrigger(@Qualifier("matchQuartetClinicsJobDetail") JobDetailBean jobDetail,
                                                          @Value("${quartetMatch.cron}") String cronExpression) {
        return createCronTrigger(jobDetail, cronExpression);
    }

    /**
     * The JobDetail bean for matchQuartetClinicsJob
     * @return
     * @throws Exception
     */
    @Bean("matchQuartetClinicsJobDetail")
    public JobDetailBean matchQuartetClinicsJobDetail() throws Exception {
        return createJobDetailBean(matchQuartetClinicsJob);
    }

    /**
     * The JobDetail bean for importMasterClinicData
     * @return
     * @throws Exception
     */
    @Bean("loadMasterJobDetail")
    public JobDetailBean createLoadMasterJobDetail() throws Exception {
        return createJobDetailBean(importMasterClinicData);
    }

    private JobDetailBean createJobDetailBean(Job job) throws Exception {
        JobDetailBean factoryBean = new JobDetailBean();
        factoryBean.setJobClass(JobLauncherDetails.class);
        Map<String,Object> jobData = new HashMap<>();
        jobData.put("job", job);
        factoryBean.setJobDataAsMap(jobData);
        return factoryBean;
    }

    /**
     * Method for creation of triggers
     */
    private CronTriggerFactoryBean createCronTrigger(JobDetailBean jobDetail, String cronExpression) {
        CronTriggerFactoryBean factoryBean = new CronTriggerFactoryBean();
        factoryBean.setJobDetail(jobDetail);
        factoryBean.setCronExpression(cronExpression);
        return factoryBean;
    }

}
