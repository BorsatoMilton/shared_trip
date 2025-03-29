package data;

import java.sql.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionDB {
    private static final Logger logger = LoggerFactory.getLogger(ConnectionDB.class);
    private static final String URL = "jdbc:mysql://localhost:3306/shared_trip";
    private static final String USUARIO = "root";
    private static final String CONTRASENA = "admin";
    private static ConnectionDB instancia;
    private int conectados = 0;
    private Connection conn = null;

    private ConnectionDB() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            logger.error("Error cargando el driver de MySQL", e);
        }
    }

    public static synchronized ConnectionDB getInstancia() {
        if (instancia == null) {
            instancia = new ConnectionDB();
        }
        return instancia;
    }

    public synchronized Connection getConn() {
        try {
            if (conn == null || conn.isClosed()) {
                conn = DriverManager.getConnection(URL, USUARIO, CONTRASENA);
                conectados = 0;
            }
        } catch (SQLException e) {
            logger.error("Error al obtener la conexión a la base de datos", e);
        }
        conectados++;
        return conn;
    }

    public synchronized void releaseConn() {
        conectados--;
        try {
            if (conectados <= 0 && conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            logger.error("Error al cerrar la conexión a la base de datos", e);
        }
    }
}

