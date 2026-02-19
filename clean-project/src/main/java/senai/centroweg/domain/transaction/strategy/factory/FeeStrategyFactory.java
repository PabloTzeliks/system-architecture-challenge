package senai.centroweg.domain.transaction.strategy.factory;

import senai.centroweg.domain.transaction.exception.InvalidTaxException;
import senai.centroweg.domain.transaction.model.TransactionType;
import senai.centroweg.domain.transaction.strategy.FeeCalculationStrategy;
import senai.centroweg.domain.transaction.strategy.impl.CreditCardStrategy;
import senai.centroweg.domain.transaction.strategy.impl.DebitCardStrategy;
import senai.centroweg.domain.transaction.strategy.impl.PixStrategy;
import senai.centroweg.domain.transaction.strategy.impl.TedStrategy;

public class FeeStrategyFactory {

    public static FeeCalculationStrategy get(TransactionType type) {

        if (type == null) {
            throw new InvalidTaxException("Tipo de Transação nulo.");
        }

         return switch (type) {

            case PIX -> new PixStrategy();
            case TED -> new TedStrategy();
            case DEBIT_CARD -> new DebitCardStrategy();
            case CREDIT_CARD -> new CreditCardStrategy();

            default -> throw new InvalidTaxException("Tipo de Transação inválido.");
        };
    }
}
