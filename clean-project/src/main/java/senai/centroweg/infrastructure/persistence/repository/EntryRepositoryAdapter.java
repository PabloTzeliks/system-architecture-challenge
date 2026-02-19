package senai.centroweg.infrastructure.persistence.repository;

import senai.centroweg.domain.entry.model.Entry;
import senai.centroweg.domain.entry.ports.EntryRepositoryPort;

import java.util.List;
import java.util.UUID;

public class EntryRepositoryAdapter implements EntryRepositoryPort {

    @Override
    public List<Entry> saveAll(List<Entry> entries) {
        return List.of();
    }

    @Override
    public List<Entry> findAllByAccountId(UUID id) {
        return List.of();
    }
}
