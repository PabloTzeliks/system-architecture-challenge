package senai.centroweg.infrastructure.persistence.database;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface RowExtractor<T> {
    T extractor(ResultSet rs) throws SQLException;
}
