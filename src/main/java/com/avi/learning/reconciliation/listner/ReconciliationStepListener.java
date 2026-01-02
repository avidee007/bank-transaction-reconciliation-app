package com.avi.learning.reconciliation.listner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.listener.StepExecutionListener;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.stereotype.Component;

@Component
public class ReconciliationStepListener implements StepExecutionListener {

  private static final Logger log = LoggerFactory.getLogger(ReconciliationStepListener.class);

  @Override
  public void beforeStep(StepExecution stepExecution) {
    log.info("Starting step: {}", stepExecution.getStepName());
  }

  @Override
  public ExitStatus afterStep(StepExecution stepExecution) {
    log.info(
        """
                Step completed  : {}
                Read Count      : {}
                Write Count     : {}
                Skip Count      : {}
                Commit Count    : {}
                Rollback Count  : {}
                Exit Status     : {}
                """,
        stepExecution.getStepName(),
        stepExecution.getReadCount(),
        stepExecution.getWriteCount(),
        stepExecution.getSkipCount(),
        stepExecution.getCommitCount(),
        stepExecution.getRollbackCount(),
        stepExecution.getExitStatus());

    return stepExecution.getExitStatus();
  }
}
