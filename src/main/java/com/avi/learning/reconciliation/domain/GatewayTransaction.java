package com.avi.learning.reconciliation.domain;

import java.math.BigDecimal;

public record GatewayTransaction(
        String txnId, String accountNo, BigDecimal amount, java.util.Date txnDate) {}
