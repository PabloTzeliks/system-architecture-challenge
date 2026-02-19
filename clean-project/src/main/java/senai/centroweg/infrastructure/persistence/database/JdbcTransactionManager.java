package senai.centroweg.infrastructure.persistence.database;

import senai.centroweg.application.transaction.manager.TransactionManager;
import senai.centroweg.infrastructure.persistence.exception.DatabaseException;

import java.sql.Connection;
import java.sql.SQLException;

public class JdbcTransactionManager implements TransactionManager {

    private final DataSource dataSource;

    public JdbcTransactionManager(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void execute(Runnable action) {

        if (ConnectionContext.get() != null) {
            action.run();
            return;
        }

        try (Connection connection = dataSource.getConnection()) {

            connection.setAutoCommit(false);
            ConnectionContext.set(connection);

            try {
                action.run();
                connection.commit();

            } catch (Exception e) {
                throw new DatabaseException("Erro ao executar transação. Rollback executado. Observe os detalhes: " + e.getMessage(), e);
            } finally {
                connection.setAutoCommit(true);
            }

        } catch (SQLException ex) {
            throw new DatabaseException("Erro ao gerenciar conexão do banco de dados. Observe os detalhes: " + ex.getMessage(), ex);
        } finally {
            ConnectionContext.clear();
        }
    }
}
