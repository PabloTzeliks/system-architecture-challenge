package senai.centroweg.application.transaction.usecase;

import senai.centroweg.domain.account.exception.AccountNotFoundException;
import senai.centroweg.domain.account.model.Account;
import senai.centroweg.domain.account.ports.AccountRepositoryPort;
import senai.centroweg.domain.transaction.model.Transaction;
import senai.centroweg.domain.transaction.model.TransactionType;
import senai.centroweg.domain.transaction.ports.TransactionRepositoryPort;
import senai.centroweg.domain.transaction.strategy.FeeCalculationStrategy;
import senai.centroweg.domain.transaction.strategy.factory.FeeStrategyFactory;

import java.math.BigDecimal;
import java.util.UUID;

public class TransferFundsUseCase {
    AccountRepositoryPort accountRepository;
    TransactionRepositoryPort transactionRepository;

    public TransferFundsUseCase(AccountRepositoryPort accountRepository, TransactionRepositoryPort transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    public Transaction execute(UUID senderId, UUID receiverId, BigDecimal amount, TransactionType type) {

        FeeCalculationStrategy feeCalculationStrategy = FeeStrategyFactory.get(type);

        Account senderAccount = accountRepository.findById(senderId)
                .orElseThrow(() -> new AccountNotFoundException("Conta com ID " + senderId + " não encontrada"));
        Account receiverAccount = accountRepository.findById(receiverId)
                .orElseThrow(() -> new AccountNotFoundException("Conta com ID " + senderId + " não encontrada"));

        Transaction transaction = Transaction.create(senderId,receiverId,amount,type,feeCalculationStrategy);

        senderAccount.withdraw(transaction.getTotalAmount());
        receiverAccount.deposit(transaction.getRawAmount());

        transactionRepository.save(transaction);

        return transaction;
    }
}
