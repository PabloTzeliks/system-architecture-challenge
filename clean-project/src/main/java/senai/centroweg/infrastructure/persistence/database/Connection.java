package senai.centroweg.infrastructure.persistence.database;

import java.sql.DriverManager;
import java.sql.SQLException;

public class Connection {

    private static final String URL = "";
    private static final String USER = "";
    private static final String PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
