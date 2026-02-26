package senai.centroweg.domain.transaction.strategy.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import senai.centroweg.domain.transaction.model.TransactionType;
import senai.centroweg.domain.transaction.strategy.factory.FeeStrategyFactory;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FeeStrategyTest {

    @Test
    @DisplayName("PIX: Deve retornar taxa zero independentemente do valor")
    void pixShouldAlwaysBeFree() {
        PixStrategy strategy = new PixStrategy();
        BigDecimal amount = new BigDecimal("1000.00");

        BigDecimal fee = strategy.calculate(amount, TransactionType.PIX);

        assertEquals(0, BigDecimal.ZERO.compareTo(fee), "Pix deve ter taxa 0");
    }

    @Test
    @DisplayName("TED: Deve calcular 0.5% de taxa corretamente")
    void tedShouldCalculateCorrectly() {
        TedStrategy strategy = new TedStrategy();
        BigDecimal amount = new BigDecimal("1000.00");

        BigDecimal fee = strategy.calculate(amount, TransactionType.TED);

        assertEquals(0, new BigDecimal("5.00").compareTo(fee));
    }

    @Test
    @DisplayName("DEBIT_CARD: Deve calcular 1% de taxa corretamente")
    void debitCardShouldCalculateCorrectly() {
        DebitCardStrategy strategy = new DebitCardStrategy();
        BigDecimal amount = new BigDecimal("150.00");

        BigDecimal fee = strategy.calculate(amount, TransactionType.DEBIT_CARD);

        assertEquals(0, new BigDecimal("1.50").compareTo(fee));
    }

    @Test
    @DisplayName("CREDIT_CARD: Deve calcular 3.5% de taxa corretamente")
    void creditCardShouldCalculateCorrectly() {
        CreditCardStrategy strategy = new CreditCardStrategy();
        BigDecimal amount = new BigDecimal("200.00");

        BigDecimal fee = strategy.calculate(amount, TransactionType.CREDIT_CARD);

        assertEquals(0, new BigDecimal("7.00").compareTo(fee));
    }

    @ParameterizedTest
    @DisplayName("Devem retornar zero se o valor da transação for zero")
    @CsvSource({
            "PIX", "TED", "DEBIT_CARD", "CREDIT_CARD"
    })
    void allStrategiesShouldReturnZeroForZeroAmount(TransactionType type) {
        // Teste dinâmico
        var strategy = FeeStrategyFactory.get(type);
        BigDecimal fee = strategy.calculate(BigDecimal.ZERO, type);
        assertEquals(0, BigDecimal.ZERO.compareTo(fee));
    }
}