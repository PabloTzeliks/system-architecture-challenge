package senai.centroweg.domain.entry.model;

import lombok.Getter;
import senai.centroweg.domain.transaction.model.Transaction;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
public class Entry {
    private final UUID id;
    private final UUID accountId;
    private final UUID transactionId;
    private final BigDecimal amount;
    private final Instant creationDate;

    public Entry(UUID id, UUID accountId, UUID transactionId, BigDecimal amount, Instant creationDate) {
        this.id = id;
        this.accountId = accountId;
        this.transactionId = transactionId;
        this.amount = amount;
        this.creationDate = creationDate;
    }

    public Entry(UUID accountId, UUID transactionId, BigDecimal amount, Instant creationDate) {
        this.id = UUID.randomUUID();
        this.accountId = accountId;
        this.transactionId = transactionId;
        this.amount = amount;
        this.creationDate = Instant.now();
    }

    public static Entry createDebit(BigDecimal amount, Transaction transaction) {
        return new Entry(
                transaction.getSenderAccountId(),
                transaction.getId(),
                amount.negate(),
                Instant.now()
        );
    }

    public static Entry createCredit(BigDecimal amount, Transaction transaction) {
        return new Entry(
                transaction.getReceiverAccountId(),
                transaction.getId(),
                amount,
                Instant.now()
        );
    }
}
