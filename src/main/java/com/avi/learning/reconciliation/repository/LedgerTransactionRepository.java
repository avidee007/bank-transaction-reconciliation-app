package com.avi.learning.reconciliation.repository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LedgerTransactionRepository
    extends JpaRepository<LedgerTransactionEntity, String> {}
