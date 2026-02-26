package senai.centroweg.domain.account.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import senai.centroweg.domain.account.exception.InsufficientFundsException;
import senai.centroweg.domain.common.exception.DomainException;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    private Account account;
    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        account = new Account(userId);
    }

    @Test
    @DisplayName("Deve calcular o saldo somando créditos e débitos da lista")
    void shouldCalculateBalanceFromList() {
        // Arrange
        List<BigDecimal> entries = List.of(
                new BigDecimal("100.00"),
                new BigDecimal("50.00"),
                new BigDecimal("-30.00")
        );

        // Act
        account.calculateCurrentBalance(entries);

        // Assert
        assertEquals(0, new BigDecimal("120.00").compareTo(account.getCurrentBalance()));
    }

    @Test
    @DisplayName("Deve resultar em saldo zero quando a lista de entradas for vazia")
    void shouldBeZeroWhenNoEntries() {
        account.calculateCurrentBalance(List.of());
        assertEquals(BigDecimal.ZERO, account.getCurrentBalance());
    }

    @Test
    @DisplayName("Deve realizar depósito com sucesso")
    void shouldDepositSuccessfully() {
        account.deposit(new BigDecimal("100.00"));
        assertEquals(0, new BigDecimal("100.00").compareTo(account.getCurrentBalance()));
    }

    @Test
    @DisplayName("Deve lançar exceção ao depositar valor negativo ou zero")
    void shouldThrowExceptionForInvalidDeposit() {
        assertThrows(DomainException.class, () -> account.deposit(new BigDecimal("-10.00")));
        assertThrows(DomainException.class, () -> account.deposit(BigDecimal.ZERO));
    }

    @Test
    @DisplayName("Deve realizar saque quando houver saldo suficiente")
    void shouldWithdrawSuccessfully() {
        // Arrange
        account.deposit(new BigDecimal("100.00"));

        // Act
        account.withdraw(new BigDecimal("40.00"));

        // Assert
        assertEquals(0, new BigDecimal("60.00").compareTo(account.getCurrentBalance()));
    }

    @Test
    @DisplayName("Deve lançar exceção ao sacar valor maior que o saldo disponível")
    void shouldThrowExceptionForInsufficientFunds() {
        account.deposit(new BigDecimal("50.00"));

        assertThrows(InsufficientFundsException.class, () ->
                account.withdraw(new BigDecimal("50.01"))
        );
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar sacar valor negativo ou zero")
    void shouldThrowExceptionForInvalidWithdraw() {
        assertThrows(DomainException.class, () -> account.withdraw(new BigDecimal("-5.00")));
        assertThrows(DomainException.class, () -> account.withdraw(BigDecimal.ZERO));
    }
}