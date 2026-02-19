package senai.centroweg.infrastructure.persistence.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@FunctionalInterface
public interface RowExtractor<T> {
    T run(PreparedStatement preparedStatement) throws SQLException;
}
