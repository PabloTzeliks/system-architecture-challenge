package senai.centroweg.domain.transaction.strategy;

import senai.centroweg.domain.transaction.model.TransactionType;

import java.math.BigDecimal;

public interface FeeCalculationStrategy {
    BigDecimal calculate(BigDecimal amount, TransactionType type);
}
