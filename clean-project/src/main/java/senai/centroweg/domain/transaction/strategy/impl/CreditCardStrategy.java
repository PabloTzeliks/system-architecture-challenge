package senai.centroweg.domain.transaction.strategy.impl;

import senai.centroweg.domain.transaction.model.TransactionType;
import senai.centroweg.domain.transaction.strategy.FeeCalculationStrategy;

import java.math.BigDecimal;

public class CreditCardStrategy implements FeeCalculationStrategy {

    private static final BigDecimal CREDIT_RATE = new BigDecimal("0.035");

    @Override
    public BigDecimal calculate(BigDecimal amount, TransactionType type) {

        return amount.multiply(CREDIT_RATE);
    }
}
