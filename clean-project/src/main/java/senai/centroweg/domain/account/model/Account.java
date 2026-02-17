package senai.centroweg.domain.account.model;

import lombok.Data;
import lombok.Getter;
import senai.centroweg.domain.account.exception.DomainException;
import senai.centroweg.domain.account.exception.InsufficientFundsException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
public class Account {

    private final UUID id;
    private BigDecimal currentBalance;
    private final Instant createdAt;

    public Account(UUID id, Instant createdAt) {
        this.id = id;
        this.createdAt = createdAt;
        this.currentBalance = BigDecimal.ZERO;
    }

    public Account(Instant createdAt) {
        this.id = UUID.randomUUID();
        this.currentBalance = BigDecimal.ZERO;
        this.createdAt = createdAt;
    }

    public void calculateCurrentBalance(List<BigDecimal> amounts) {

        this.currentBalance = amounts.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void deposit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainException("Valor de crédito deve ser positivo");
        }

        this.currentBalance = this.currentBalance.add(amount);
    }

    public void withdraw(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainException("Valor de débito deve ser positivo");
        }

        if (this.currentBalance.compareTo(amount) < 0) {
            throw new InsufficientFundsException("Saldo insuficiente");
        }

        this.currentBalance = this.currentBalance.subtract(amount);
    }
}
