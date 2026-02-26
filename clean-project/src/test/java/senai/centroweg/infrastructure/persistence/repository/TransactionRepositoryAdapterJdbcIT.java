package senai.centroweg.infrastructure.persistence.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import senai.centroweg.domain.transaction.model.Transaction;
import senai.centroweg.domain.transaction.model.TransactionType;
import senai.centroweg.domain.transaction.strategy.impl.TedStrategy;
import senai.centroweg.infrastructure.persistence.AbstractDatabaseTest;
import senai.centroweg.infrastructure.persistence.database.QueryExecutor;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TransactionRepositoryAdapterJdbcIT extends AbstractDatabaseTest {

    private TransactionRepositoryAdapter transactionRepository;
    private QueryExecutor queryExecutor;

    @BeforeEach
    void setUp() {
        queryExecutor = new QueryExecutor(dataSource);
        transactionRepository = new TransactionRepositoryAdapter(queryExecutor);
    }

    private void createDummyAccounts(UUID senderId, UUID receiverId) throws Exception {
        String insertAccount = "INSERT INTO accounts (id, user_id, name, created_at) VALUES (?, ?, 'Teste', ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(insertAccount)) {

            // Remetente
            ps.setObject(1, senderId);
            ps.setObject(2, UUID.randomUUID());
            ps.setTimestamp(3, Timestamp.from(Instant.now()));
            ps.addBatch();

            // Destinatário
            ps.setObject(1, receiverId);
            ps.setObject(2, UUID.randomUUID());
            ps.setTimestamp(3, Timestamp.from(Instant.now()));
            ps.addBatch();

            ps.executeBatch();
        }
    }

    @Test
    @DisplayName("Deve salvar uma transação com sucesso no banco de dados")
    void shouldSaveTransactionSuccessfully() throws Exception {
        // Arrange
        UUID senderId = UUID.randomUUID();
        UUID receiverId = UUID.randomUUID();
        createDummyAccounts(senderId, receiverId);

        Transaction transaction = Transaction.create(
                senderId,
                receiverId,
                new BigDecimal("1000.00"),
                TransactionType.TED,
                new TedStrategy()
        );

        transaction.confirm();

        // Act
        Transaction saved = transactionRepository.save(transaction);

        // Assert: Validando diretamente no banco para provar que o Adapter funcionou
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM transactions WHERE id = ?")) {

            ps.setObject(1, saved.getId());
            ResultSet rs = ps.executeQuery();

            assertTrue(rs.next(), "A transação deveria estar no banco");
            assertEquals(senderId, rs.getObject("sender_account_id", UUID.class));
            assertEquals(receiverId, rs.getObject("receiver_account_id", UUID.class));

            // Verifica os valores financeiros
            assertEquals(0, new BigDecimal("1000.00").compareTo(rs.getBigDecimal("raw_amount")));
            assertEquals(0, new BigDecimal("5.00").compareTo(rs.getBigDecimal("fee_amount")));
            assertEquals(0, new BigDecimal("1005.00").compareTo(rs.getBigDecimal("total_amount")));

            assertEquals("TED", rs.getString("type"));
            assertNotNull(rs.getTimestamp("confirmation_at"), "A data de confirmação não deve ser nula");
        }
    }
}