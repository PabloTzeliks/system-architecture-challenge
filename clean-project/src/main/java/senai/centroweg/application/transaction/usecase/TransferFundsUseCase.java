package senai.centroweg.application.transaction.usecase;

import senai.centroweg.domain.account.exception.AccountNotFoundException;
import senai.centroweg.domain.account.model.Account;
import senai.centroweg.domain.account.ports.AccountRepositoryPort;
import senai.centroweg.domain.transaction.model.Transaction;

import java.math.BigDecimal;
import java.util.UUID;

public class TransferFundsUseCase {
    AccountRepositoryPort accountRepository;

    public TransferFundsUseCase(AccountRepositoryPort accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Transaction execute(UUID senderId, UUID receiverId, BigDecimal amount) {

        Account senderAccount = accountRepository.findById(senderId)
                .orElseThrow(() -> new AccountNotFoundException("Conta com ID " + senderId + " não encontrada"));
        Account receiverAccount = accountRepository.findById(receiverId)
                .orElseThrow(() -> new AccountNotFoundException("Conta com ID " + senderId + " não encontrada"));


    }
}
