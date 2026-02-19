package senai.centroweg.application.transaction.usecase;

import senai.centroweg.application.transaction.command.TransferCommand;
import senai.centroweg.application.transaction.manager.TransactionManager;
import senai.centroweg.domain.account.exception.AccountNotFoundException;
import senai.centroweg.domain.account.model.Account;
import senai.centroweg.domain.account.ports.AccountRepositoryPort;
import senai.centroweg.domain.entry.model.Entry;
import senai.centroweg.domain.entry.ports.EntryRepositoryPort;
import senai.centroweg.domain.transaction.model.Transaction;
import senai.centroweg.domain.transaction.ports.TransactionRepositoryPort;
import senai.centroweg.domain.transaction.strategy.FeeCalculationStrategy;
import senai.centroweg.domain.transaction.strategy.factory.FeeStrategyFactory;

import java.util.List;

public class TransferFundsUseCase {

    private final AccountRepositoryPort accountRepository;
    private final TransactionRepositoryPort transactionRepository;
    private final EntryRepositoryPort entryRepository;
    private final TransactionManager transactionManager;

    public TransferFundsUseCase(AccountRepositoryPort accountRepository,
                                TransactionRepositoryPort transactionRepository,
                                EntryRepositoryPort entryRepository,
                                TransactionManager transactionManager) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.entryRepository = entryRepository;
        this.transactionManager = transactionManager;
    }

    public Transaction execute(TransferCommand cmd) {

        FeeCalculationStrategy strategy = FeeStrategyFactory.get(cmd.type());

        Account sender = accountRepository.findById(cmd.senderId())
                .orElseThrow(() -> new AccountNotFoundException("Remetente não encontrado."));

        Account receiver = accountRepository.findById(cmd.receiverId())
                .orElseThrow(() -> new AccountNotFoundException("Destinatário não encontrado."));

        sender.calculateCurrentBalance(entryRepository.findAllByAccountId(sender.getId()));

        Transaction transaction = Transaction.create(
                sender.getId(),
                receiver.getId(),
                cmd.amount(),
                cmd.type(),
                strategy
        );

        sender.withdraw(transaction.getTotalAmount());

        List<Entry> entries = transaction.generateEntries();

        transactionManager.execute(() -> {
            transactionRepository.save(transaction);
            entryRepository.saveAll(entries);
        });

        return transaction;
    }
}
