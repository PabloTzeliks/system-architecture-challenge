package senai.centroweg.domain.account.ports;

import senai.centroweg.domain.account.model.Account;

import java.util.UUID;

public interface AccountRepositoryPort {

    Account save(Account account);

    Account findById(UUID id);
}
