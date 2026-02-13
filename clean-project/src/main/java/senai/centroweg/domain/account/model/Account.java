package senai.centroweg.domain.account.model;

import lombok.Data;
import senai.centroweg.domain.account.exception.DomainException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
public class Account {

    private final UUID id;
    private BigDecimal balance;
    private final Instant createdAt;

    public Account(UUID id, BigDecimal balance, Instant createdAt) {
        this.id = id;
        this.balance = balance;
        this.createdAt = createdAt;
    }

    public Account(BigDecimal balance, Instant createdAt) {
        this.id = UUID.randomUUID();
        this.balance = balance;
        this.createdAt = createdAt;
    }

    public void credit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainException("Valor de crédito deve ser positivo");
        }

        this.balance = this.balance.add(amount);
    }

    public void debit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainException("Valor de débito deve ser positivo");
        }

        if (this.balance.compareTo(amount) < 0) {
            throw new DomainException("Saldo insuficiente");
        }

        this.balance = this.balance.subtract(amount);
    }
}
