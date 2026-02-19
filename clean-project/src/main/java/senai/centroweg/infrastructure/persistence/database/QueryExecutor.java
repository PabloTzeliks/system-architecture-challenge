package senai.centroweg.infrastructure.persistence.database;

import senai.centroweg.infrastructure.persistence.exception.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class QueryExecutor {

    private final DataSource dataSource;

    public QueryExecutor(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public <R> R extract(final String query, final StatementCallback<R> callback) {

        Connection activeConnection = ConnectionContext.get();
        boolean isTransactional = (activeConnection != null);

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = isTransactional ? activeConnection : dataSource.getConnection();

            ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.clearParameters();

            return callback.run(ps);

        } catch (SQLException ex) {
            throw new DatabaseException("Erro executando query: " + ex.getMessage(), ex);
        } finally {
            if (ps != null) {
                try { ps.close(); } catch (SQLException ignored) {}
            }

            if (!isTransactional && conn != null) {
                try { conn.close(); } catch (SQLException ignored) {}
            }
        }
    }
}
