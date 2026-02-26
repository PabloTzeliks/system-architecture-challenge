package senai.centroweg.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import senai.centroweg.application.transaction.command.TransferCommand;
import senai.centroweg.application.transaction.usecase.TransferFundsUseCase;
import senai.centroweg.domain.account.model.Account;
import senai.centroweg.domain.entry.model.Entry;
import senai.centroweg.domain.transaction.model.Transaction;
import senai.centroweg.domain.transaction.model.TransactionType;
import senai.centroweg.domain.transaction.strategy.impl.PixStrategy;
import senai.centroweg.domain.user.model.User;
import senai.centroweg.domain.user.ports.UserRepositoryPort;
import senai.centroweg.infrastructure.persistence.AbstractDatabaseTest;
import senai.centroweg.infrastructure.persistence.database.JdbcTransactionManager;
import senai.centroweg.infrastructure.persistence.database.QueryExecutor;
import senai.centroweg.infrastructure.persistence.repository.AccountRepositoryAdapter;
import senai.centroweg.infrastructure.persistence.repository.EntryRepositoryAdapter;
import senai.centroweg.infrastructure.persistence.repository.TransactionRepositoryAdapter;
import senai.centroweg.infrastructure.persistence.repository.UserRepositoryAdapter;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BankingFlowE2EIT extends AbstractDatabaseTest {

    private UserRepositoryAdapter userRepository;
    private AccountRepositoryAdapter accountRepository;
    private EntryRepositoryAdapter entryRepository;
    private TransactionRepositoryAdapter transactionRepository;
    private JdbcTransactionManager transactionManager;
    private TransferFundsUseCase transferFundsUseCase;

    @BeforeEach
    void setUpAll() {
        QueryExecutor queryExecutor = new QueryExecutor(dataSource);
        transactionManager = new JdbcTransactionManager(dataSource);

        userRepository = new UserRepositoryAdapter(queryExecutor);
        accountRepository = new AccountRepositoryAdapter(queryExecutor);
        entryRepository = new EntryRepositoryAdapter(queryExecutor);
        transactionRepository = new TransactionRepositoryAdapter(queryExecutor);

        transferFundsUseCase = new TransferFundsUseCase(
                accountRepository,
                transactionRepository,
                entryRepository,
                transactionManager
        );
    }

    @Test
    @DisplayName("Simulação E2E: Fluxo completo de transações com recálculo de saldo real")
    void shouldExecuteComplexFlowAndCalculateExactBalances() {
        // Arrange

        // 1. Criamos os Usuários no Domínio
        User aliceUser = new User("Alice");
        User bobUser = new User("Bob");

        userRepository.save(aliceUser);
        userRepository.save(bobUser);

        // 2. Criamos as Contas amarradas aos Usuários
        Account alice = new Account(aliceUser.getId());
        Account bob = new Account(bobUser.getId());

        // Persistimos as contas no banco H2 (Assumindo que o Adapter foi ajustado para não pedir 'name')
        accountRepository.save(alice);
        accountRepository.save(bob);

        // 3. Setup de Saldo Inicial: Simulamos um aporte de 1000.00 para a Alice
        // Criamos uma transação fantasma apenas para satisfazer a Foreign Key do banco
        Transaction fundingTx = Transaction.create(
                alice.getId(), alice.getId(), new BigDecimal("1000.00"), TransactionType.PIX, new PixStrategy()
        );
        transactionRepository.save(fundingTx);

        // Injetamos o dinheiro na conta da Alice via Entry de crédito
        Entry initialDeposit = Entry.createCredit(new BigDecimal("1000.00"), fundingTx);
        entryRepository.saveAll(List.of(initialDeposit));


        // Act

        // Transferência 1: Alice manda PIX de 100 para Bob (Taxa 0)
        // Alice: 1000 - 100 = 900
        // Bob: 0 + 100 = 100
        transferFundsUseCase.execute(new TransferCommand(
                alice.getId(), bob.getId(), new BigDecimal("100.00"), TransactionType.PIX
        ));

        // Transferência 2: Alice manda TED de 200 para Bob (Taxa 0.5% = 1.00)
        // Alice: 900 - 201 = 699
        // Bob: 100 + 200 = 300
        transferFundsUseCase.execute(new TransferCommand(
                alice.getId(), bob.getId(), new BigDecimal("200.00"), TransactionType.TED
        ));

        // Transferência 3: Bob devolve 50 para Alice via Cartão de Débito (Taxa 1% = 0.50)
        // Bob: 300 - 50.50 = 249.50
        // Alice: 699 + 50 = 749
        transferFundsUseCase.execute(new TransferCommand(
                bob.getId(), alice.getId(), new BigDecimal("50.00"), TransactionType.DEBIT_CARD
        ));

        // Assert

        // Nós não confiamos na memória. Vamos buscar os extratos salvos no banco!
        List<BigDecimal> aliceEntries = entryRepository.findAllByAccountId(alice.getId());
        List<BigDecimal> bobEntries = entryRepository.findAllByAccountId(bob.getId());

        // Usamos o próprio domínio para reidratar o saldo atual com base nos lançamentos
        alice.calculateCurrentBalance(aliceEntries);
        bob.calculateCurrentBalance(bobEntries);

        // Afirmações finais com precisão de casas decimais
        assertEquals(0, new BigDecimal("749.00").compareTo(alice.getCurrentBalance()),
                "O saldo da Alice não bateu após as deduções de envio e recebimento.");

        assertEquals(0, new BigDecimal("249.50").compareTo(bob.getCurrentBalance()),
                "O saldo do Bob não bateu após as taxas do Cartão de Débito.");
    }
}