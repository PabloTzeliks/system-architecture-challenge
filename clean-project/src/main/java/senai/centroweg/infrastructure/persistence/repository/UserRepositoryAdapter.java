package senai.centroweg.infrastructure.persistence.repository;

import senai.centroweg.domain.user.model.User;
import senai.centroweg.domain.user.ports.UserRepositoryPort;
import senai.centroweg.infrastructure.persistence.database.QueryExecutor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public class UserRepositoryAdapter implements UserRepositoryPort {

    private final QueryExecutor database;

    public UserRepositoryAdapter(QueryExecutor database) {
        this.database = database;
    }

    private User mapRow(ResultSet rs) throws SQLException {
        return new User(
                UUID.fromString(rs.getString("id")),
                rs.getString("username")
        );
    }

    @Override
    public User save(User user) {

        String query = """
                INSERT INTO
                users (id, username)
                VALUES (?, ?)
                """;

        return database.extract(query, ps -> {
            ps.setObject(1, user.getId());
            ps.setString(2, user.getUsername());

            ps.executeUpdate();
            return user;
        });
    }

    @Override
    public Optional<User> findById(UUID id) {

        String query = """
                SELECT id, username
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
