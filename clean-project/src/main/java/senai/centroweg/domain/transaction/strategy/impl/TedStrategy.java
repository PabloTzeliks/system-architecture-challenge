package senai.centroweg.domain.transaction.strategy.impl;

import senai.centroweg.domain.transaction.model.TransactionType;
import senai.centroweg.domain.transaction.strategy.FeeCalculationStrategy;

import java.math.BigDecimal;

public class TedStrategy implements FeeCalculationStrategy {

    private static final BigDecimal TED_RATE = new BigDecimal("0.005");

    @Override
    public BigDecimal calculate(BigDecimal amount, TransactionType type) {

        return amount.multiply(TED_RATE);
    }
}
