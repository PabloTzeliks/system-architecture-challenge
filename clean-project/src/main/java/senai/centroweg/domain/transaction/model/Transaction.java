package senai.centroweg.domain.transaction.model;

import lombok.Data;
import senai.centroweg.domain.entry.model.Entry;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
public class Transaction {

    private final UUID id;
    private final UUID senderAccountId;
    private final UUID receiverAccountId;
    private final BigDecimal rawAmount;
    private BigDecimal totalAmount;
    private final TransactionType type;
    private final Instant createdAt;
    private Instant confirmationAt;

    public Transaction(UUID id, UUID senderAccountId,
                       UUID receiverAccountId,
                       BigDecimal rawAmount,
                       BigDecimal totalAmount,
                       TransactionType type,
                       Instant createdAt,
                       Instant confirmationAt) {
        this.id = id;
        this.senderAccountId = senderAccountId;
        this.receiverAccountId = receiverAccountId;
        this.rawAmount = rawAmount;
        this.totalAmount = totalAmount;
        this.type = type;
        this.createdAt = createdAt;
        this.confirmationAt = confirmationAt;
    }

    public Transaction(UUID senderAccountId,
                       UUID receiverAccountId,
                       BigDecimal rawAmount,
                       TransactionType type) {

        this.id = UUID.randomUUID();
        this.senderAccountId = senderAccountId;
        this.receiverAccountId = receiverAccountId;
        this.rawAmount = rawAmount;
        this.type = type;
        this.createdAt = Instant.now();
    }

    public void applyFee(BigDecimal feeValue) {
        if (feeValue.compareTo(BigDecimal.ZERO) < 0) {
            throw new TransactionDomainException("Taxa não pode ser negativa");
        }

        this.feeAmount = feeValue;
        this.totalAmount = this.rawAmount.add(this.feeAmount);
    }

    public List<Entry> generateEntries() {

        Entry debit = Entry.createDebit(
                this.totalAmount,
                this
        );

        Entry credit = Entry.createCredit(
                this.rawAmount,
                this
        );

        return List.of(debit, credit);
    }
}
