package com.avi.learning.reconciliation.tasklet;

import com.avi.learning.reconciliation.domain.ReconciliationStatus;
import com.avi.learning.reconciliation.repository.ReconciliationResultRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.StepContribution;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Component
public class ReconciliationSummaryTasklet implements Tasklet {

  private static final Logger log = LoggerFactory.getLogger(ReconciliationSummaryTasklet.class);

  private final ReconciliationResultRepository repository;

  public ReconciliationSummaryTasklet(ReconciliationResultRepository repository) {
    this.repository = repository;
  }

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
    log.info("Generating reconciliation summary");
    repository
        .countByStatus()
        .forEach(
            row -> {
              ReconciliationStatus status = (ReconciliationStatus) row[0];
              Long count = (Long) row[1];
              log.info("Status: {} | Count: {}", status, count);
            });

    return RepeatStatus.FINISHED;
  }
}
