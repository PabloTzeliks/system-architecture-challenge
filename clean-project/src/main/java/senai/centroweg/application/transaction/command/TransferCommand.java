package senai.centroweg.application.transaction.command;

import senai.centroweg.domain.transaction.model.TransactionType;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferCommand(UUID senderId,
                              UUID receiverId,
                              BigDecimal amount,
                              TransactionType type) {
}
