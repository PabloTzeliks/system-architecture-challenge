package senai.centroweg.infrastructure.persistence.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@FunctionalInterface
public interface StatementCallback<T> {
    T run(PreparedStatement ps) throws SQLException;
}
