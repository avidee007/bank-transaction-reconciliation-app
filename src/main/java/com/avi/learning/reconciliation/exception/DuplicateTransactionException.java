package com.avi.learning.reconciliation.exception;

public class DuplicateTransactionException extends RuntimeException {
  public DuplicateTransactionException(String txnId) {
    super("Duplicate gateway transaction detected: " + txnId);
  }
}
