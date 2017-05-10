package com.qt.jobs;

/**
 * Created by asrivastava on 5/9/17.
 */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

@Component
public class JobLauncherDetails extends QuartzJobBean {
    private static Log log = LogFactory.getLog(JobLauncherDetails.class);
    /**
     * Special key in job data map for the name of a job to run.
     */
    protected static final String JOB_NAME = "job";
    protected Job job;

    @Autowired
    public JobLauncher jobLauncher;

    /**
     * The job instance which is executed by the Quartz Trigger bean is passed on to the JobDetail.
     * Using JobLauncher, the job is executed.
     * @param context
     */
    @SuppressWarnings("unchecked")
    protected void executeInternal(JobExecutionContext context) {
        Map<String, Object> jobDataMap = context.getMergedJobDataMap();
        job = (Job) jobDataMap.get(JOB_NAME);
        log.info("Quartz trigger firing with Spring Batch jobName="+job.getName());

        JobParameters jobParameters = getJobParametersFromJobMap(jobDataMap);
        logJobParameters(jobParameters);
        try {
            jobLauncher.run(job, jobParameters);
        }
        catch (JobExecutionException e) {
            log.error("Could not execute job.", e);
        }
    }

    /*
     * Copy parameters that are of the correct type over to
     * {@link JobParameters}, ignoring jobName.
     *
     * @return a {@link JobParameters} instance
     */
    protected JobParameters getJobParametersFromJobMap(Map<String, Object> jobDataMap) {

        JobParametersBuilder builder = new JobParametersBuilder();

        for (Entry<String, Object> entry : jobDataMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            //Add a timestamp param, because spring batch will ignore job runs with same parameter values.
            builder.addDate("ts", new Date());
            if (value instanceof String && !key.equals(JOB_NAME)) {
                builder.addString(key, (String) value);
            }
            else if (value instanceof Float || value instanceof Double) {
                builder.addDouble(key, ((Number) value).doubleValue());
            }
            else if (value instanceof Integer || value instanceof Long) {
                builder.addLong(key, ((Number)value).longValue());
            }
            else if (value instanceof Date) {
                builder.addDate(key, (Date) value);
            }
            else {
                log.debug("JobDataMap contains values which are not job parameters (ignoring).");
            }
        }

        return builder.toJobParameters();

    }

    protected void logJobParameters(JobParameters jobParameters) {
        if (log.isInfoEnabled()) {
            Map<String, JobParameter> parameters = jobParameters.getParameters();
            for (Entry<String, JobParameter> entry : parameters.entrySet()) {
                log.info("key= " + entry.getKey() + " value="+entry.getValue());
            }
        }

    }


}
