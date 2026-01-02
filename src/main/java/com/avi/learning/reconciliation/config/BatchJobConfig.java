package com.avi.learning.reconciliation.config;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BatchJobConfig {

  @Bean
  public Job reconciliationJob(
      JobRepository jobRepository, Step reconciliationStep, Step reconciliationSummaryStep) {
    return new JobBuilder("bankTransactionReconciliationJob", jobRepository)
        .start(reconciliationStep)
        .on("*")
        .to(reconciliationSummaryStep)
        .end()
        .build();
  }
}
