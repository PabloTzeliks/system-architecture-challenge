package senai.centroweg.infrastructure.persistence.repository;

import senai.centroweg.domain.transaction.model.Transaction;
import senai.centroweg.domain.transaction.ports.TransactionRepositoryPort;
import senai.centroweg.infrastructure.persistence.database.QueryExecutor;
import senai.centroweg.infrastructure.persistence.exception.DatabaseException;

import java.sql.SQLException;
import java.sql.Timestamp;

public class TransactionRepositoryAdapter implements TransactionRepositoryPort {

    QueryExecutor database;

    public TransactionRepositoryAdapter(QueryExecutor database) {
        this.database = database;
    }

    @Override
    public Transaction save(Transaction transaction) {
        String query = """
                INSERT INTO transactions (
                    id, senderAccountId, receiverAccountId, rawAmount, 
                    feeAmount, totalAmount, transactionType, createdAt, confirmationAt
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
                ps.setObject(9, null);
            }

            ps.executeUpdate();
            return transaction;
        });
    }
}
