package senai.centroweg.infrastructure.account.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import senai.centroweg.domain.transaction.model.Transaction;
import senai.centroweg.domain.transaction.model.TransactionType;
import senai.centroweg.domain.transaction.strategy.impl.PixStrategy;
import senai.centroweg.infrastructure.persistence.AbstractDatabaseTest;
import senai.centroweg.infrastructure.persistence.database.JdbcTransactionManager;
import senai.centroweg.infrastructure.persistence.database.QueryExecutor;
import senai.centroweg.infrastructure.persistence.exception.DatabaseException;
import senai.centroweg.infrastructure.persistence.repository.TransactionRepositoryAdapter;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AccountRepositoryAdapterJdbcIT extends AbstractDatabaseTest {

    private JdbcTransactionManager transactionManager;
    private TransactionRepositoryAdapter transactionRepository;

    @BeforeEach
    void setUp() {
        QueryExecutor queryExecutor = new QueryExecutor(dataSource);
        transactionRepository = new TransactionRepositoryAdapter(queryExecutor);
        transactionManager = new JdbcTransactionManager(dataSource);
    }

    @Test
    @DisplayName("Deve realizar rollback e não salvar a transação no banco se ocorrer erro no execute")
    void shouldRollbackWhenExceptionOccurs() throws Exception {
        // Arrange
        Transaction tx = Transaction.create(
                UUID.randomUUID(),
                UUID.randomUUID(),
                new BigDecimal("100.00"),
                TransactionType.PIX,
                new PixStrategy()
        );

        // Act
        assertThrows(DatabaseException.class, () -> {
            transactionManager.execute(() -> {
                transactionRepository.save(tx);

                throw new RuntimeException("Erro forçado na regra de negócio!");
            });
        });

        int count = 0;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT count(*) FROM transactions");
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                count = rs.getInt(1);
            }
        }

        assertEquals(0, count, "A tabela deveria estar vazia devido ao Rollback!");
    }
}