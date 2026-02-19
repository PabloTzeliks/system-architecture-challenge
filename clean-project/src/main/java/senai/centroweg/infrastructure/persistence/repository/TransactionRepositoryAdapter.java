package senai.centroweg.infrastructure.persistence.repository;

import senai.centroweg.domain.transaction.model.Transaction;
import senai.centroweg.domain.transaction.ports.TransactionRepositoryPort;
import senai.centroweg.infrastructure.persistence.database.QueryExecutor;

import java.sql.Timestamp;
import java.sql.Types;

public class TransactionRepositoryAdapter implements TransactionRepositoryPort {

    QueryExecutor database;

    public TransactionRepositoryAdapter(QueryExecutor database) {
        this.database = database;
    }

    @Override
    public Transaction save(Transaction transaction) {
        String query = """
                INSERT INTO transactions (
                    id,
                    sender_account_id,
                    receiver_account_id,
                    raw_amount,
                    fee_amount,
                    total_amount,
                    transaction_type,
                    created_at,
                    confirmation_at
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        return database.extract(query, ps -> {

            ps.setObject(1, transaction.getId());
            ps.setObject(2, transaction.getSenderAccountId());
            ps.setObject(3, transaction.getReceiverAccountId());
            ps.setBigDecimal(4, transaction.getRawAmount());
            ps.setBigDecimal(5, transaction.getFeeAmount());
            ps.setBigDecimal(6, transaction.getTotalAmount());
            ps.setString(7, transaction.getType().name());
            ps.setTimestamp(8, Timestamp.from(transaction.getCreatedAt()));

            if (transaction.getConfirmationAt() != null) {
                ps.setTimestamp(9, Timestamp.from(transaction.getConfirmationAt()));
            } else {
                ps.setNull(9, Types.TIMESTAMP);
            }

            ps.executeUpdate();
            return transaction;
        });
    }
}
