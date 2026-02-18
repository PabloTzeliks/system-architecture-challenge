package senai.centroweg.domain.transaction.exception;

import senai.centroweg.domain.common.exception.DomainException;

public class InvalidTaxException extends DomainException {
    public InvalidTaxException(String message) {
        super(message);
    }
}
