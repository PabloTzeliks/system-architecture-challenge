package senai.centroweg.infrastructure.persistence.repository;

import senai.centroweg.domain.account.model.Account;
import senai.centroweg.domain.account.ports.AccountRepositoryPort;
import senai.centroweg.infrastructure.persistence.database.QueryExecutor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

public class AccountRepositoryAdapter implements AccountRepositoryPort {

    QueryExecutor database;

    public AccountRepositoryAdapter(QueryExecutor database) {
        this.database = database;
    }

    private Account mapRow(ResultSet rs) throws SQLException {
        return new Account(
                UUID.fromString(rs.getString("id")),
                UUID.fromString(rs.getString("userId")),
                rs.getTimestamp("createdAt").toInstant()
        );
    }

    @Override
    public Account save(Account account) {

        String query = """
                INSERT INTO
                accounts (id, userId, createdAt)
                VALUES (?, ?, ?, ?)
                """;

        return database.extract(query, ps -> {
            ps.setObject(1, account.getId());
            ps.setObject(2, account.getUserId());
            ps.setTimestamp(4, Timestamp.from(account.getCreatedAt()));

            ps.executeUpdate();
            return account;
        });
    }

    @Override
    public Optional<Account> findById(UUID id) {

        String query = """
                SELECT id, userId, currentBalance, createdAt
                FROM users
                WHERE id = ?
                """;

        return database.extract(query, ps -> {
            ps.setObject(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }

                return Optional.empty();
            }
        });
    }
}
