package senai.centroweg.infrastructure.persistence;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.Statement;

public abstract class AbstractDatabaseTest {

    protected JdbcDataSource dataSource;

    @BeforeEach
    void setupDatabase() throws Exception {

        // 1. Instancia o DataSource apenas uma vez por teste
        dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:testdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1");
        dataSource.setUser("sa");
        dataSource.setPassword("");

        // 2. Limpa e reconstrói o schema antes de CADA teste
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {

            // Garante que não sobrou tabela ou dado do teste anterior
            stmt.execute("DROP ALL OBJECTS");

            // Lê o arquivo SQL e cria as tabelas do zero
            String schema = Files.readString(Path.of("src/test/resources/schema.sql"));
            stmt.execute(schema);
        }
    }
}
