package senai.centroweg.infrastructure.persistence.database;

import java.sql.Connection;

public class ConnectionContext {

    private static ThreadLocal<Connection> CONTEXT = new ThreadLocal<>();

    public static void set(Connection connection) {
        CONTEXT.set(connection);
    }

    public static Connection get() {
        return CONTEXT.get();
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
