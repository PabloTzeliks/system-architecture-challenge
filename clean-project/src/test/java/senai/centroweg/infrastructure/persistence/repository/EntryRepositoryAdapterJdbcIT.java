package senai.centroweg.infrastructure.persistence.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import senai.centroweg.domain.entry.model.Entry;
import senai.centroweg.domain.transaction.model.Transaction;
import senai.centroweg.domain.transaction.model.TransactionType;
import senai.centroweg.domain.transaction.strategy.impl.PixStrategy;
import senai.centroweg.infrastructure.persistence.AbstractDatabaseTest;
import senai.centroweg.infrastructure.persistence.database.QueryExecutor;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EntryRepositoryAdapterJdbcIT extends AbstractDatabaseTest {

    private EntryRepositoryAdapter entryRepository;
    private QueryExecutor queryExecutor;

    @BeforeEach
    void setUp() {
        queryExecutor = new QueryExecutor(dataSource);
        entryRepository = new EntryRepositoryAdapter(queryExecutor);
    }

    private void insertDummyAccount(UUID accountId) throws Exception {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO accounts (id, user_id, name, created_at) VALUES (?, ?, 'User', ?)")) {
            ps.setObject(1, accountId);
            ps.setObject(2, UUID.randomUUID());
            ps.setTimestamp(3, java.sql.Timestamp.from(Instant.now()));
            ps.executeUpdate();
        }
    }

    private void insertDummyTransaction(Transaction tx) throws Exception {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO transactions (id, sender_account_id, receiver_account_id, raw_amount, fee_amount, total_amount, type, created_at) " +
                             "VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
            ps.setObject(1, tx.getId());
            ps.setObject(2, tx.getSenderAccountId());
            ps.setObject(3, tx.getReceiverAccountId());
            ps.setBigDecimal(4, tx.getRawAmount());
            ps.setBigDecimal(5, tx.getFeeAmount());
            ps.setBigDecimal(6, tx.getTotalAmount());
            ps.setString(7, tx.getType().name());
            ps.setTimestamp(8, java.sql.Timestamp.from(tx.getCreatedAt()));
            ps.executeUpdate();
        }
    }

    @Test
    @DisplayName("Deve salvar debito e credito através dos Factory Methods do dominio e buscar valores")
    void shouldSaveAndFindAllAmountsByAccountId() throws Exception {
        // Arrange
        UUID myAccountId = UUID.randomUUID();
        UUID otherAccountId = UUID.randomUUID();

        // 1. Prepara as contas no DB para as FKs
        insertDummyAccount(myAccountId);
        insertDummyAccount(otherAccountId);

        // 2. Monta as transações pelo Domínio
        // Transação 1: Alguém manda PIX de 100.00 para mim (Serei o Receiver)
        Transaction incomingTx = Transaction.create(
                otherAccountId, myAccountId, new BigDecimal("100.00"), TransactionType.PIX, new PixStrategy()
        );
        // Transação 2: Eu mando PIX de 30.00 para alguém (Serei o Sender)
        Transaction outgoingTx = Transaction.create(
                myAccountId, otherAccountId, new BigDecimal("30.00"), TransactionType.PIX, new PixStrategy()
        );

        // 3. Salva fisicamente as transações para a Entry não reclamar da FK
        insertDummyTransaction(incomingTx);
        insertDummyTransaction(outgoingTx);

        // 4. Cria as Entries usando o seu padrão de projeto perfeito
        Entry creditEntry = Entry.createCredit(incomingTx.getTotalAmount(), incomingTx); // +100.00
        Entry debitEntry = Entry.createDebit(outgoingTx.getTotalAmount(), outgoingTx);   // -30.00

        // Act
        entryRepository.saveAll(List.of(creditEntry, debitEntry));
        List<BigDecimal> amounts = entryRepository.findAllByAccountId(myAccountId);

        // Assert
        assertEquals(2, amounts.size(), "A conta deveria ter exatamente 2 lançamentos");

        // Confirma se os métodos abs() e negate() funcionaram na prática dentro do repositório
        assertTrue(amounts.stream().anyMatch(val -> val.compareTo(new BigDecimal("100.00")) == 0), "Deveria conter o crédito de 100");
        assertTrue(amounts.stream().anyMatch(val -> val.compareTo(new BigDecimal("-30.00")) == 0), "Deveria conter o débito de -30");
    }
}