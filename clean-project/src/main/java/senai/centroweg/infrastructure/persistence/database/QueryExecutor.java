package senai.centroweg.infrastructure.persistence.database;

import senai.centroweg.infrastructure.persistence.exception.DatabaseException;

import java.sql.SQLException;
import java.sql.Statement;

public class QueryExecutor {

    <R> R extract(final String query, final RowExtractor<R> extractor) {

        try (var connection = DataSource.getConnection();
             var preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.clearParameters();

            return extractor.run(preparedStatement);
        } catch (SQLException ex) {
            throw new DatabaseException(ex.getMessage());
        }
    }
}
