package senai.centroweg.domain.entry.ports;

import senai.centroweg.domain.entry.model.Entry;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface EntryRepositoryPort {
    List<Entry> saveAll(List<Entry> entries);

    List<BigDecimal> findAllByAccountId(UUID id);
}
