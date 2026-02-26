package senai.centroweg.application.transaction.usecase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import senai.centroweg.application.transaction.command.TransferCommand;
import senai.centroweg.application.transaction.manager.TransactionManager;
import senai.centroweg.domain.account.exception.AccountNotFoundException;
import senai.centroweg.domain.account.exception.InsufficientFundsException;
import senai.centroweg.domain.account.model.Account;
import senai.centroweg.domain.account.ports.AccountRepositoryPort;
import senai.centroweg.domain.entry.ports.EntryRepositoryPort;
import senai.centroweg.domain.transaction.model.Transaction;
import senai.centroweg.domain.transaction.model.TransactionType;
import senai.centroweg.domain.transaction.ports.TransactionRepositoryPort;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferFundsUseCaseTest {

    @Mock private AccountRepositoryPort accountRepository;
    @Mock private TransactionRepositoryPort transactionRepository;
    @Mock private EntryRepositoryPort entryRepository;
    @Mock private TransactionManager transactionManager;

    @InjectMocks
    private TransferFundsUseCase useCase;

    private UUID senderId;
    private UUID receiverId;
    private TransferCommand validCommand;

    @BeforeEach
    void setUp() {
        senderId = UUID.randomUUID();
        receiverId = UUID.randomUUID();
        validCommand = new TransferCommand(senderId, receiverId, new BigDecimal("100.00"), TransactionType.PIX);

        // Configuração crucial: Faz o TransactionManager executar o código que estiver dentro da Lambda
        lenient().doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(transactionManager).execute(any(Runnable.class));
    }

    @Test
    @DisplayName("Deve realizar a transferência com sucesso quando todos os dados forem válidos")
    void shouldTransferFundsSuccessfully() {
        // Arrange
        Account sender = new Account(senderId);
        Account receiver = new Account(receiverId);

        // Simula que o remetente tem 500.00 de saldo (reidratação)
        when(accountRepository.findById(senderId)).thenReturn(Optional.of(sender));
        when(accountRepository.findById(receiverId)).thenReturn(Optional.of(receiver));
        when(entryRepository.findAllByAccountId(any(UUID.class))).thenReturn(List.of(new BigDecimal("500.00")));

        // Act
        Transaction result = useCase.execute(validCommand);

        // Assert
        assertNotNull(result);
        assertTrue(result.isConfirmed());
        assertEquals(0, new BigDecimal("100.00").compareTo(result.getRawAmount()));

        // Verifica se os repositórios foram chamados
        verify(transactionRepository, times(1)).save(any(Transaction.class));
        verify(entryRepository, times(1)).saveAll(anyList());
    }

    @Test
    @DisplayName("Deve lançar exceção se o remetente não existir")
    void shouldThrowExceptionWhenSenderNotFound() {
        when(accountRepository.findById(senderId)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> useCase.execute(validCommand));

        // Garante que o processo parou antes de chegar no banco
        verify(transactionManager, never()).execute(any());
    }

    @Test
    @DisplayName("Deve lançar exceção se não houver saldo suficiente após a reidratação")
    void shouldThrowExceptionWhenInsufficientFunds() {
        // Arrange
        Account sender = new Account(senderId);
        Account receiver = new Account(receiverId);

        when(accountRepository.findById(senderId)).thenReturn(Optional.of(sender));
        when(accountRepository.findById(receiverId)).thenReturn(Optional.of(receiver));

        // Simula saldo insuficiente (apenas 50.00 para uma transferência de 100.00)
        when(entryRepository.findAllByAccountId(any(UUID.class))).thenReturn(List.of(new BigDecimal("50.00")));

        // Act & Assert
        assertThrows(InsufficientFundsException.class, () -> useCase.execute(validCommand));

        verify(transactionRepository, never()).save(any());
    }
}