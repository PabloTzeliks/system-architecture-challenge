package senai.centroweg.domain.account.model;

import lombok.Data;

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
}
