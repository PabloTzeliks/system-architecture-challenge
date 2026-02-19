package senai.centroweg.infrastructure.persistence.exception;

import senai.centroweg.infrastructure.common.exception.InfrastructureException;

public class DatabaseException extends InfrastructureException {
    public DatabaseException(String message) {
        super(message);
    }
}
