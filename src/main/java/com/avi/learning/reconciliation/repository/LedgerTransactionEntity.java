package com.avi.learning.reconciliation.repository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "LEDGER_TRANSACTION")
public class LedgerTransactionEntity {

  @Id
  @Column(name = "txn_id")
  private String txnId;

  @Column(nullable = false)
  private BigDecimal amount;

  protected LedgerTransactionEntity() {}

  public String getTxnId() {
    return txnId;
  }

  public BigDecimal getAmount() {
    return amount;
  }
}
