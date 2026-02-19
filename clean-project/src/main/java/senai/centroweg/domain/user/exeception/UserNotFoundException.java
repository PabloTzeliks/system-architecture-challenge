package senai.centroweg.domain.user.exeception;

import senai.centroweg.domain.common.exception.BusinessRuleException;

public class UserNotFoundException extends BusinessRuleException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
