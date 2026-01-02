package com.avi.learning.reconciliation.config;

import com.avi.learning.reconciliation.domain.GatewayTransaction;
import com.avi.learning.reconciliation.exception.DuplicateTransactionException;
import com.avi.learning.reconciliation.exception.InvalidTransactionException;
import com.avi.learning.reconciliation.listener.ReconciliationStepListener;
import com.avi.learning.reconciliation.repository.ReconciliationResultEntity;
import com.avi.learning.reconciliation.tasklet.ReconciliationSummaryTasklet;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.ChunkOrientedStepBuilder;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class StepsConfig {

  @Bean
  public Step reconciliationStep(
      JobRepository jobRepository,
      ItemReader<GatewayTransaction> reader,
      ItemProcessor<GatewayTransaction, ReconciliationResultEntity> processor,
      ItemWriter<ReconciliationResultEntity> writer,
      ReconciliationStepListener listener) {

    return new ChunkOrientedStepBuilder<GatewayTransaction, ReconciliationResultEntity>(
            "reconciliationStep", jobRepository, 5)
        .reader(reader)
        .processor(processor)
        .retry(TransientDataAccessException.class)
        .retryLimit(3)
        .skip(InvalidTransactionException.class)
        .skip(DuplicateTransactionException.class)
        .skipLimit(10)
        .writer(writer)
        .faultTolerant()
        .listener(listener)
        .build();
  }

  @Bean
  public Step reconciliationSummaryStep(
      JobRepository jobRepository,
      PlatformTransactionManager transactionManager,
      ReconciliationSummaryTasklet tasklet) {

    return new StepBuilder("reconciliationSummaryStep", jobRepository)
        .tasklet(tasklet, transactionManager)
        .build();
  }
}
