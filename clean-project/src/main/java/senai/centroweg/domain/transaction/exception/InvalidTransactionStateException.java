package senai.centroweg.domain.transaction.exception;

import senai.centroweg.domain.common.exception.DomainException;

public class InvalidTransactionStateException extends DomainException {
    public InvalidTransactionStateException(String message) {
        super(message);
    }
}
