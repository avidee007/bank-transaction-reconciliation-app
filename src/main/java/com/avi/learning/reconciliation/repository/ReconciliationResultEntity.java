package com.avi.learning.reconciliation.repository;

import com.avi.learning.reconciliation.domain.ReconciliationStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "RECONCILIATION_RESULT",
    uniqueConstraints =
        @UniqueConstraint(
            name = "gateway-trx-id-uk",
            columnNames = {"gatewayTxnId"}))
public class ReconciliationResultEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String gatewayTxnId;
  private String ledgerTxnId;

  @Enumerated(EnumType.STRING)
  private ReconciliationStatus status;

  private String remarks;

  private LocalDateTime reconciledAt;

  public ReconciliationResultEntity() {}

  public void setGatewayTxnId(String gatewayTxnId) {
    this.gatewayTxnId = gatewayTxnId;
  }

  public void setLedgerTxnId(String ledgerTxnId) {
    this.ledgerTxnId = ledgerTxnId;
  }

  public void setStatus(ReconciliationStatus status) {
    this.status = status;
  }

  public void setRemarks(String remarks) {
    this.remarks = remarks;
  }

  public void setReconciledAt(LocalDateTime reconciledAt) {
    this.reconciledAt = reconciledAt;
  }
}
