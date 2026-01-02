package com.avi.learning.reconciliation.writer;

import com.avi.learning.reconciliation.repository.ReconciliationResultEntity;
import com.avi.learning.reconciliation.repository.ReconciliationResultRepository;
import org.springframework.batch.infrastructure.item.data.RepositoryItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JpaWriter {

  @Bean
  public RepositoryItemWriter<ReconciliationResultEntity> reconciliationWriter(
      ReconciliationResultRepository repository) {
    return new RepositoryItemWriter<>(repository);
  }
}
