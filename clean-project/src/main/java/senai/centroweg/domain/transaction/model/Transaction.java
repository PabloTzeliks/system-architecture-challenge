package senai.centroweg.domain.transaction.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
public class Transaction {

    private final UUID id;
    private final UUID senderAccountId;
    private final UUID receiverAccountId;
    private final BigDecimal amount;
    private final TransactionType type;
    private final Instant createdAt;
    private Instant confirmationAt;

    public Transaction(UUID id, UUID senderAccountId, UUID receiverAccountId, BigDecimal amount, TransactionType type, Instant createdAt, Instant confirmationAt) {
        this.id = id;
        this.senderAccountId = senderAccountId;
        this.receiverAccountId = receiverAccountId;
        this.amount = amount;
        this.type = type;
        this.createdAt = createdAt;
        this.confirmationAt = confirmationAt;
    }

    public Transaction(UUID senderAccountId, UUID receiverAccountId, BigDecimal amount, TransactionType type, Instant createdAt, Instant confirmationAt) {
        this.id = UUID.randomUUID();
        this.senderAccountId = senderAccountId;
        this.receiverAccountId = receiverAccountId;
        this.amount = amount;
        this.type = type;
        this.createdAt = createdAt;
        this.confirmationAt = confirmationAt;
    }
}
