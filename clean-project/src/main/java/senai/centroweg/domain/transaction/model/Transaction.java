package senai.centroweg.domain.transaction.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class Transaction {

    private final UUID id;
    private final UUID senderAccountId;
    private final UUID receiverAccountId;
    private final BigDecimal amount;

}
