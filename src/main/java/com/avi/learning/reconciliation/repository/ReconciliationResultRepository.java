package com.avi.learning.reconciliation.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReconciliationResultRepository
    extends JpaRepository<ReconciliationResultEntity, Long> {

  boolean existsByGatewayTxnId(String gatewayTxnId);

  @Query(value = "SELECT r.status, COUNT(r) FROM ReconciliationResultEntity r GROUP BY r.status")
  List<Object[]> countByStatus();
}
