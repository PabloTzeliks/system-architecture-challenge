package senai.centroweg.domain.transaction.strategy.impl;

import senai.centroweg.domain.transaction.model.TransactionType;
import senai.centroweg.domain.transaction.strategy.FeeCalculationStrategy;

import java.math.BigDecimal;

public class PixStrategy implements FeeCalculationStrategy {

    @Override
    public BigDecimal calculate(BigDecimal amount, TransactionType type) {

        BigDecimal percentage = BigDecimal.valueOf(TransactionType.PIX.getTax());
        return amount.multiply(percentage);
    }
}
