package senai.centroweg.infrastructure.persistence.exception;

import senai.centroweg.infrastructure.common.exception.InfrastructureException;

import java.sql.SQLException;

public class DatabaseException extends InfrastructureException {
    public DatabaseException(String message, SQLException exception) {
        super(message);
    }

    public DatabaseException(String message, Exception exception) {
        super(message);
    }
}
