package senai.centroweg.infrastructure.persistence;

import org.junit.jupiter.api.BeforeEach;
import senai.centroweg.infrastructure.persistence.database.DataSource;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.Statement;

public abstract class AbstractDatabaseTest {

    protected DataSource dataSource;

    @BeforeEach
    void setupDatabase() throws Exception {
        // Instancia a sua classe com os dados do H2
        dataSource = new DataSource(
                "jdbc:h2:mem:testdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
                "sa",
                ""
        );

        // Limpa e constrói o banco em memória
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("DROP ALL OBJECTS");
            String schema = Files.readString(Path.of("src/test/resources/schema.sql"));
            stmt.execute(schema);
        }
    }
}
