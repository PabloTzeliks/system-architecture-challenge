package senai.centroweg.domain.transaction.strategy.impl;

import senai.centroweg.domain.transaction.model.TransactionType;
import senai.centroweg.domain.transaction.strategy.FeeCalculationStrategy;

import java.math.BigDecimal;

public class PixStrategy implements FeeCalculationStrategy {

    private static final BigDecimal PIX_RATE = new BigDecimal("0");

    @Override
    public BigDecimal calculate(BigDecimal amount, TransactionType type) {

        return amount.multiply(PIX_RATE);
    }
}
