package senai.centroweg.domain.account.exception;

import senai.centroweg.domain.common.exception.BusinessRuleException;

public class AccountNotFoundException extends BusinessRuleException {
    public AccountNotFoundException(String message) {
        super(message);
    }
}
