package senai.centroweg.infrastructure.persistence.repository;

import senai.centroweg.domain.entry.model.Entry;
import senai.centroweg.domain.entry.ports.EntryRepositoryPort;
import senai.centroweg.infrastructure.persistence.database.QueryExecutor;
import senai.centroweg.infrastructure.persistence.exception.DatabaseException;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class EntryRepositoryAdapter implements EntryRepositoryPort {

    private final QueryExecutor database;

    public EntryRepositoryAdapter(QueryExecutor database) {
        this.database = database;
    }

    private List<Entry> mapRow(ResultSet rs) throws SQLException {

        List<Entry> entryList = new ArrayList<>();

        while (rs.next()) {
            entryList.add(new Entry(
                    UUID.fromString(rs.getString("id")),
                    UUID.fromString(rs.getString("accountId")),
                    UUID.fromString(rs.getString("transactionId")),
                    rs.getBigDecimal("amount"),
                    rs.getTimestamp("creationDate").toInstant()
            ));
        }
        return entryList;
    }

    @Override
    public List<Entry> saveAll(List<Entry> entries) {
        String query = """
                INSERT INTO
                entries(accountId, transactionId, amount, creationDate)
                VALUES(?,?,?,?)
                """;

        try {
            for (Entry entry : entries) {
                database.extract(query, ps -> {
                    ps.setObject(1, entry.getAccountId());
                    ps.setObject(2, entry.getTransactionId());
                    ps.setBigDecimal(3, entry.getAmount());
                    ps.setTimestamp(4, java.sql.Timestamp.from(entry.getCreationDate()));

                    return ps.executeUpdate();
                });
            }
            return entries;
        } catch (Exception e) {
            throw new DatabaseException("Erro ao salvar o lote de lançamento", e);
        }
    }

    @Override
    public List<Entry> findAllByAccountId(UUID id) {
        String query = """
                SELECT id, accountId, transactionId, amount, creationDate
                FROM entries
                WHERE id = ?
                """;

        return database.extract(query, ps -> {
            try(ResultSet rs = ps.executeQuery();) {

                return mapRow(rs);
            } catch (SQLException e) {

                throw new DatabaseException("Erro ao processar o ResultSet",e);
            }
        });
    }
}
