package senai.centroweg.domain.transaction.model;

import lombok.Getter;

@Getter
public enum TransactionType {

    PIX(0),
    TED(0.5),
    CREDIT_CARD(1),
    DEBIT_CARD(3.5);

    private final double tax;

    TransactionType(double tax) {
        this.tax = tax;
    }
}
