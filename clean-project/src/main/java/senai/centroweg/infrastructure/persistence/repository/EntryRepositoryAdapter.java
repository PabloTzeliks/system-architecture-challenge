package senai.centroweg.infrastructure.persistence.repository;

import senai.centroweg.domain.entry.model.Entry;
import senai.centroweg.domain.entry.ports.EntryRepositoryPort;
import senai.centroweg.infrastructure.persistence.database.QueryExecutor;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EntryRepositoryAdapter implements EntryRepositoryPort {

    private final QueryExecutor database;

    public EntryRepositoryAdapter(QueryExecutor database) {
        this.database = database;
    }

    private Entry mapRow(ResultSet rs) throws SQLException {

        return new Entry(
                UUID.fromString(rs.getString("id")),
                UUID.fromString(rs.getString("account_id")),
                UUID.fromString(rs.getString("transaction_id")),
                rs.getBigDecimal("amount"),
                rs.getTimestamp("created_at").toInstant()
        );
    }

    @Override
    public List<Entry> saveAll(List<Entry> entries) {

        String query = """
                INSERT INTO
                entries(id, account_id, transaction_id, amount, created_at)
                VALUES(?,?,?,?,?)
                """;

        return database.extract(query, ps -> {
            for (Entry entry : entries) {
                ps.setObject(1, entry.getId());
                ps.setObject(2, entry.getAccountId());
                ps.setObject(3, entry.getTransactionId());
                ps.setBigDecimal(4, entry.getAmount());
                ps.setTimestamp(5, Timestamp.from(entry.getCreatedAt()));

                ps.addBatch();
            }

            ps.executeBatch();

            return entries;
        });
    }

    @Override
    public List<BigDecimal> findAllByAccountId(UUID accountId) {

        String query = """
            SELECT id, account_id, transaction_id, amount, created_at
            FROM entries
            WHERE account_id = ?
            ORDER BY created_at ASC
            """;

        return database.extract(query, ps -> {
            ps.setObject(1, accountId);

            try (ResultSet rs = ps.executeQuery()) {
                List<BigDecimal> entries = new ArrayList<>();

                while (rs.next()) {
                    entries.add(rs.getBigDecimal("amount"));
                }

                return entries;
            }
        });
    }
}
