package senai.centroweg.infrastructure.persistence.repository;

import senai.centroweg.domain.transaction.model.Transaction;
import senai.centroweg.domain.transaction.ports.TransactionRepositoryPort;

public class TransactionRepositoryAdapter implements TransactionRepositoryPort {

    @Override
    public Transaction save(Transaction transaction) {
        return null;
    }
}
