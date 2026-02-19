package senai.centroweg.application.account.usecase;

import senai.centroweg.application.account.command.NewAccountCommand;
import senai.centroweg.application.account.manager.AccountManager;
import senai.centroweg.domain.account.exception.AccountNotFoundException;
import senai.centroweg.domain.account.model.Account;
import senai.centroweg.domain.account.ports.AccountRepositoryPort;
import senai.centroweg.domain.user.model.User;
import senai.centroweg.domain.user.ports.UserRepositoryPort;

public class AddNewAccountUseCase {

    private final UserRepositoryPort userRepository;
    private final AccountRepositoryPort accountRepository;
    private final AccountManager accountManager;

    public AddNewAccountUseCase(UserRepositoryPort userRepository,
                                AccountRepositoryPort accountRepository,
                                AccountManager accountManager) {

        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.accountManager = accountManager;
    }

    public Account execute(NewAccountCommand accountCommand) {
        User user = userRepository.findById(accountCommand.userId())
                .orElseThrow(() -> new AccountNotFoundException("Usuário não foi encontrado"));

        Account account = new Account(user.getId());

        accountManager.execute(() -> {
            accountRepository.save(account);
        });

        return account;
    }
}
