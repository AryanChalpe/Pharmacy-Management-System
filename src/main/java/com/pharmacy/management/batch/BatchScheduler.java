package com.pharmacy.management.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class BatchScheduler {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job expiryJob;

    @Scheduled(cron = "0 45 9 * * ?") // Every day at 9:45 AM
    public void runExpiryCheck() {
        try {
            jobLauncher.run(
                    expiryJob,
                    new JobParametersBuilder()
                            .addLong("time", System.currentTimeMillis())
                            .toJobParameters());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
