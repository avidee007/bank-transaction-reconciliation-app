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
                \n======== JOB RUN REPORT ==========================
                Job StartTime   : {}
                Step completed  : {}
                Read Count      : {}
                Write Count     : {}
                Skip Count      : {}
                Commit Count    : {}
                Rollback Count  : {}
                Exception Thrown: {}
                Job Status      : {}
                Job EndTime     : {}
                ========= END OF REPORT ==========================
                """,
        stepExecution.getStartTime(),
        stepExecution.getStepName(),
        stepExecution.getReadCount(),
        stepExecution.getWriteCount(),
        stepExecution.getSkipCount(),
        stepExecution.getCommitCount(),
        stepExecution.getRollbackCount(),
        stepExecution.getFailureExceptions(),
        stepExecution.getStatus(),
        stepExecution.getEndTime());

    return stepExecution.getExitStatus();
  }
}
