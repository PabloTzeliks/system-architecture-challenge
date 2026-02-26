package senai.centroweg.infrastructure.account.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import senai.centroweg.domain.account.model.Account;
import senai.centroweg.domain.account.ports.AccountRepositoryPort;
import senai.centroweg.infrastructure.persistence.AbstractDatabaseTest;
import senai.centroweg.infrastructure.persistence.database.QueryExecutor;
import senai.centroweg.infrastructure.persistence.repository.AccountRepositoryAdapter;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AccountRepositoryAdapterJdbcIT extends AbstractDatabaseTest {

    private AccountRepositoryPort repository;

    private QueryExecutor queryExecutor;

    @BeforeEach
    void setUpRepository() {

        queryExecutor = new QueryExecutor(dataSource);

        repository = new AccountRepositoryAdapter(queryExecutor);
    }

    @Test
    @DisplayName("Deve inserir uma conta no banco e buscar com sucesso")
    void shouldInsertAndFindAccount() {
        // Arrange
        UUID accountId = UUID.randomUUID();
        Account account = new Account(accountId, UUID.randomUUID(), Instant.now());

        // Act
        repository.save(account);
        var found = repository.findById(accountId);

        // Assert
        assertTrue(found.isPresent());
        assertEquals(accountId, found.get().getId());
    }
}