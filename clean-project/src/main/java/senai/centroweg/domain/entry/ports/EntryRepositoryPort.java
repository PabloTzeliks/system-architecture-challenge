package senai.centroweg.domain.entry.ports;

import senai.centroweg.domain.entry.model.Entry;

import java.util.List;
import java.util.UUID;

public interface EntryRepositoryPort {
    List<Entry> saveAll(List<Entry> entries);

    List<Entry> findAllByAccountId(UUID id);
}
