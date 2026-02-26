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
        String insertUser = "INSERT INTO users (id, username) VALUES (?, ?)";
        String insertAccount = "INSERT INTO accounts (id, user_id, created_at) VALUES (?, ?, ?)";

        try (Connection conn = dataSource.getConnection()) {

            // 1. Gera os IDs dos Usuários
            UUID senderUserId = UUID.randomUUID();
            UUID receiverUserId = UUID.randomUUID();

            // 2. Insere os Usuários PRIMEIRO (Para satisfazer a Foreign Key)
            try (PreparedStatement psUser = conn.prepareStatement(insertUser)) {
                // User Remetente
                psUser.setObject(1, senderUserId);
                psUser.setString(2, "SenderUser");
                psUser.addBatch();

                // User Destinatário
                psUser.setObject(1, receiverUserId);
                psUser.setString(2, "ReceiverUser");
                psUser.addBatch();

                psUser.executeBatch();
            }

            // 3. Insere as Contas ligadas aos Usuários
            try (PreparedStatement psAccount = conn.prepareStatement(insertAccount)) {
                // Conta Remetente
                psAccount.setObject(1, senderId);
                psAccount.setObject(2, senderUserId); // Usa o ID do user que acabou de criar
                psAccount.setTimestamp(3, java.sql.Timestamp.from(Instant.now()));
                psAccount.addBatch();

                // Conta Destinatário
                psAccount.setObject(1, receiverId);
                psAccount.setObject(2, receiverUserId); // Usa o ID do user que acabou de criar
                psAccount.setTimestamp(3, java.sql.Timestamp.from(Instant.now()));
                psAccount.addBatch();

                psAccount.executeBatch();
            }
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