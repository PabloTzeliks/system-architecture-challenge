package senai.centroweg.domain.transaction.model;

import lombok.Getter;
import senai.centroweg.domain.entry.model.Entry;
import senai.centroweg.domain.transaction.exception.InvalidTaxException;
import senai.centroweg.domain.transaction.exception.InvalidTransactionStateException;
import senai.centroweg.domain.transaction.strategy.FeeCalculationStrategy;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
public class Transaction {

    private final UUID id;
    private final UUID senderAccountId;
    private final UUID receiverAccountId;
    private final BigDecimal rawAmount;
    private final BigDecimal feeAmount;
    private final BigDecimal totalAmount;
    private final TransactionType type;
    private final Instant createdAt;
    private Instant confirmationAt;

    private Transaction(UUID id,
                       UUID senderAccountId,
                       UUID receiverAccountId,
                       BigDecimal rawAmount,
                       BigDecimal feeAmount,
                       BigDecimal totalAmount,
                       TransactionType type,
                       Instant createdAt,
                       Instant confirmationAt) {
        this.id = id;
        this.senderAccountId = senderAccountId;
        this.receiverAccountId = receiverAccountId;
        this.rawAmount = rawAmount;
        this.feeAmount = feeAmount;
        this.totalAmount = totalAmount;
        this.type = type;
        this.createdAt = createdAt;
        this.confirmationAt = confirmationAt;
    }

    public static Transaction create(UUID senderAccountId,
                                     UUID receiverAccountId,
                                     BigDecimal amount,
                                     TransactionType type,
                                     FeeCalculationStrategy feeStrategy) {

        BigDecimal calculatedFee = feeStrategy.calculate(amount, type);
        validateFeeValue(calculatedFee);

        BigDecimal finalTotal = amount.add(calculatedFee);

        return new Transaction(
                UUID.randomUUID(),
                senderAccountId,
                receiverAccountId,
                amount,
                calculatedFee,
                finalTotal,
                type,
                Instant.now(),
                null
        );
    }

    private static void validateFeeValue(BigDecimal fee) {
        if (fee == null || fee.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidTaxException("A taxa calculada não pode ser negativa.");
        }
    }

    public void confirm() {

        if (isConfirmed()) {
            throw new InvalidTransactionStateException("Transação já foi confirmada anteriormente.");
        }

        this.confirmationAt = Instant.now();
    }

    public boolean isConfirmed() {

        return this.confirmationAt != null;
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
