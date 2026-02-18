package senai.centroweg.domain.account.exception;

import senai.centroweg.domain.common.exception.DomainException;

public class InsufficientFundsException extends DomainException {
    public InsufficientFundsException(String message) {
        super(message);
    }
}
