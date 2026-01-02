package com.avi.learning.reconciliation.domain;

public enum ReconciliationStatus {
  MATCHED,
  AMOUNT_MISMATCH,
  MISSING_IN_LEDGER,
  INVALID
}
