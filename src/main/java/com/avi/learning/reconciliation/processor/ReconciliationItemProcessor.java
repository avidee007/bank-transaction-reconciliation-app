package com.avi.learning.reconciliation.processor;

import com.avi.learning.reconciliation.domain.GatewayTransaction;
import com.avi.learning.reconciliation.domain.ReconciliationStatus;
import com.avi.learning.reconciliation.exception.DuplicateTransactionException;
import com.avi.learning.reconciliation.exception.InvalidTransactionException;
import com.avi.learning.reconciliation.repository.LedgerTransactionEntity;
import com.avi.learning.reconciliation.repository.LedgerTransactionRepository;
import com.avi.learning.reconciliation.repository.ReconciliationResultEntity;
import com.avi.learning.reconciliation.repository.ReconciliationResultRepository;
import java.time.LocalDateTime;
import java.util.Objects;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ReconciliationItemProcessor
    implements ItemProcessor<GatewayTransaction, ReconciliationResultEntity> {

  private final LedgerTransactionRepository ledgerRepository;
  private final ReconciliationResultRepository reconciliationResultRepository;

  public ReconciliationItemProcessor(
      LedgerTransactionRepository ledgerRepository,
      ReconciliationResultRepository reconciliationResultRepository) {
    this.ledgerRepository = ledgerRepository;
    this.reconciliationResultRepository = reconciliationResultRepository;
  }

  @Override
  @Transactional(readOnly = true)
  public ReconciliationResultEntity process(GatewayTransaction txn) {
    validate(txn);

    // Enables idempotency for duplicate transactions in gateway records
    if (reconciliationResultRepository.existsByGatewayTxnId(txn.txnId())) {
      throw new DuplicateTransactionException(txn.txnId());
    }
    return ledgerRepository
        .findById(txn.txnId())
        .map(ledger -> reconcile(txn, ledger))
        .orElseGet(() -> missingInLedger(txn));
  }

  private void validate(GatewayTransaction txn) {
    if (Objects.isNull(txn.txnId()) || Objects.isNull(txn.amount())) {
      throw new InvalidTransactionException("Missing mandatory fields");
    }
  }

  private ReconciliationResultEntity reconcile(
      GatewayTransaction gateway, LedgerTransactionEntity ledger) {

    boolean matched = gateway.amount().compareTo(ledger.getAmount()) == 0;

    var entity = new ReconciliationResultEntity();
    entity.setGatewayTxnId(gateway.txnId());
    entity.setStatus(matched ? ReconciliationStatus.MATCHED : ReconciliationStatus.AMOUNT_MISMATCH);
    entity.setRemarks(matched ? "Matched successfully" : "Amount mismatch");
    entity.setLedgerTxnId(ledger.getTxnId());
    entity.setReconciledAt(LocalDateTime.now());
    return entity;
  }

  private ReconciliationResultEntity missingInLedger(GatewayTransaction txn) {
    var entity = new ReconciliationResultEntity();
    entity.setGatewayTxnId(txn.txnId());
    entity.setStatus(ReconciliationStatus.MISSING_IN_LEDGER);
    entity.setRemarks("Transaction not found in ledger");
    entity.setReconciledAt(LocalDateTime.now());
    return entity;
  }
}
