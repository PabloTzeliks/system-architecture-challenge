package senai.centroweg.domain.transaction.ports;

import senai.centroweg.domain.transaction.model.Transaction;

public interface TransactionRepositoryPort {

    Transaction save(Transaction transaction);
}
