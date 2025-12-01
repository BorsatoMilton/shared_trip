package data;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionDB {

    private static ConnectionDB instancia;
    private HikariDataSource dataSource;

    private ConnectionDB() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("No se pudo cargar el driver MySQL", e);
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:3306/shared_trip");
        config.setUsername("root");
        config.setPassword("admin");

        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setIdleTimeout(30000);

        dataSource = new HikariDataSource(config);
    }

    public static synchronized ConnectionDB getInstancia() {
        if (instancia == null)
            instancia = new ConnectionDB();
        return instancia;
    }

    public Connection getConn() throws SQLException {
        return dataSource.getConnection();
    }
}
