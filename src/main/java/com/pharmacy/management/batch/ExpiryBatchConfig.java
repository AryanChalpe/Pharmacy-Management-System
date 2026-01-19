package com.pharmacy.management.batch;

import com.pharmacy.management.model.Medicine;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class ExpiryBatchConfig {

    @Bean
    public Step expiryStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            MedicineReader reader,
            MedicineProcessor processor,
            MedicineWriter writer) {
        return new StepBuilder("expiryStep", jobRepository)
                .<Medicine, Medicine>chunk(10, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public Job expiryJob(JobRepository jobRepository, Step expiryStep) {
        return new JobBuilder("expiryJob", jobRepository)
                .start(expiryStep)
                .build();
    }
}
