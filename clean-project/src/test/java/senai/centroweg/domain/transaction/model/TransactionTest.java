package senai.centroweg.domain.transaction.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import senai.centroweg.domain.entry.model.Entry;
import senai.centroweg.domain.transaction.exception.InvalidTaxException;
import senai.centroweg.domain.transaction.exception.InvalidTransactionStateException;
import senai.centroweg.domain.transaction.strategy.FeeCalculationStrategy;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionTest {

    @Mock
    private FeeCalculationStrategy feeStrategy;

    private UUID senderId;
    private UUID receiverId;
    private BigDecimal amount;

    @BeforeEach
    void setUp() {
        senderId = UUID.randomUUID();
        receiverId = UUID.randomUUID();
        amount = new BigDecimal("100.00");
    }

    @Test
    @DisplayName("Deve criar uma transação com valores calculados corretamente")
    void shouldCreateTransactionSuccessfully() {
        // Arrange: Simulando uma taxa de 5.00
        BigDecimal simulatedFee = new BigDecimal("5.00");
        when(feeStrategy.calculate(amount, TransactionType.TED)).thenReturn(simulatedFee);

        // Act
        Transaction transaction = Transaction.create(senderId, receiverId, amount, TransactionType.TED, feeStrategy);

        // Assert
        assertNotNull(transaction.getId());
        assertEquals(0, amount.compareTo(transaction.getRawAmount()));
        assertEquals(0, simulatedFee.compareTo(transaction.getFeeAmount()));
        assertEquals(0, new BigDecimal("105.00").compareTo(transaction.getTotalAmount()));
        assertFalse(transaction.isConfirmed());
        assertNull(transaction.getConfirmationAt());
    }

    @Test
    @DisplayName("Deve lançar exceção se a estratégia de taxa retornar valor negativo")
    void shouldThrowExceptionForNegativeFee() {
        // Arrange
        when(feeStrategy.calculate(amount, TransactionType.PIX)).thenReturn(new BigDecimal("-1.00"));

        // Act & Assert
        assertThrows(InvalidTaxException.class, () ->
                Transaction.create(senderId, receiverId, amount, TransactionType.PIX, feeStrategy)
        );
    }

    @Test
    @DisplayName("Deve confirmar uma transação e registrar o momento da confirmação")
    void shouldConfirmTransaction() {
        // Arrange
        when(feeStrategy.calculate(amount, TransactionType.PIX)).thenReturn(BigDecimal.ZERO);
        Transaction transaction = Transaction.create(senderId, receiverId, amount, TransactionType.PIX, feeStrategy);

        // Act
        transaction.confirm();

        // Assert
        assertTrue(transaction.isConfirmed());
        assertNotNull(transaction.getConfirmationAt());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar confirmar uma transação já confirmada")
    void shouldThrowExceptionIfAlreadyConfirmed() {
        // Arrange
        when(feeStrategy.calculate(amount, TransactionType.PIX)).thenReturn(BigDecimal.ZERO);
        Transaction transaction = Transaction.create(senderId, receiverId, amount, TransactionType.PIX, feeStrategy);
        transaction.confirm();

        // Act & Assert
        assertThrows(InvalidTransactionStateException.class, transaction::confirm);
    }

    @Test
    @DisplayName("Deve gerar exatamente dois lançamentos (Débito e Crédito) corretamente")
    void shouldGenerateCorrectEntries() {
        // Arrange
        BigDecimal fee = new BigDecimal("2.00");
        when(feeStrategy.calculate(amount, TransactionType.DEBIT_CARD)).thenReturn(fee);
        Transaction transaction = Transaction.create(senderId, receiverId, amount, TransactionType.DEBIT_CARD, feeStrategy);

        // Act
        List<Entry> entries = transaction.generateEntries();

        // Assert
        assertEquals(2, entries.size());

        // Validando o Débito (Deve ser o TotalAmount: raw + fee)
        assertTrue(entries.stream().anyMatch(e -> e.getAmount().abs().compareTo(new BigDecimal("102.00")) == 0),
                "Deveria existir um lançamento de débito do valor total");

        // Validando o Crédito (Deve ser o RawAmount: valor bruto)
        assertTrue(entries.stream().anyMatch(e -> e.getAmount().compareTo(new BigDecimal("100.00")) == 0),
                "Deveria existir um lançamento de crédito do valor bruto");
    }
}