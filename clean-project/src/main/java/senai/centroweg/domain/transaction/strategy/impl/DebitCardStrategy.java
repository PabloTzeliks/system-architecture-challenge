package senai.centroweg.domain.transaction.strategy.impl;

import senai.centroweg.domain.transaction.model.TransactionType;
import senai.centroweg.domain.transaction.strategy.FeeCalculationStrategy;

import java.math.BigDecimal;

public class DebitCardStrategy implements FeeCalculationStrategy {

    private static final BigDecimal DEBIT_RATE = new BigDecimal("0.01");

    @Override
    public BigDecimal calculate(BigDecimal amount, TransactionType type) {

        return amount.multiply(DEBIT_RATE);
    }
}
