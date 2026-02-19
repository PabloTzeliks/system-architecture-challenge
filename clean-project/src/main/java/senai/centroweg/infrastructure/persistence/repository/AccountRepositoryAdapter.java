package senai.centroweg.infrastructure.persistence.repository;

import senai.centroweg.domain.account.model.Account;
import senai.centroweg.domain.account.ports.AccountRepositoryPort;

import java.util.Optional;
import java.util.UUID;

public class AccountRepositoryAdapter implements AccountRepositoryPort {

    @Override
    public Account save(Account account) {
        return null;
    }

    @Override
    public Optional<Account> findById(UUID id) {
        return Optional.empty();
    }
}
