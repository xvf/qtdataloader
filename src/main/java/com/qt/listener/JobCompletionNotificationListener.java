package com.qt.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by asrivastava on 5/8/17.
 */
@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

    @Value("${smtp.enabled}")
    public boolean smtpEnabled;

    private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);
    private JavaMailSender javaMailSender;
    private SimpleMailMessage templateMessage;

    @Autowired
    public JobCompletionNotificationListener(JavaMailSender javaMailSender, SimpleMailMessage templateMessage) {
        this.javaMailSender = javaMailSender;
        this.templateMessage = templateMessage;
    }

    /**
     * After job, depending on the status, a text is generated and if smtp is enabled, an email is sent out for the status
     * @param jobExecution
     */
    @Override
    public void afterJob(JobExecution jobExecution) {
        Long jobId = jobExecution.getJobId();
        Date startTime = jobExecution.getStartTime();
        Date endTime = jobExecution.getEndTime();
        SimpleMailMessage mailMessage = new SimpleMailMessage(templateMessage);
        String text= "";
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            text = "Job "+ jobId+" completed Successfully. Start time: "+startTime.getTime() + " End Time: "+ endTime.getTime();
        } else {
            text = "Job "+ jobId+" failed. Reason: "+ jobExecution.getFailureExceptions() +" Start time: "+startTime.getTime() + " End Time: "+ endTime.getTime();

        }
        /**
         * If SMTP Server is setup, turn on this flag an change the smtp server values for notification messages
         * upon Job completion
         */
        if(smtpEnabled) {
            mailMessage.setText(text);
            javaMailSender.send(mailMessage);
            return;
        }

        log.info(text);
    }
}