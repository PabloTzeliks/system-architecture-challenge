package senai.centroweg.domain.transaction.model;

import lombok.Getter;

@Getter
public enum TransactionType {

    PIX,
    TED,
    CREDIT_CARD,
    DEBIT_CARD;
}
