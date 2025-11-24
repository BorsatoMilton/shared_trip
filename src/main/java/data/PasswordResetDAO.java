package data;

import java.sql.*;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import entidades.*;

public class PasswordResetDAO {

    private static final Logger logger = LoggerFactory.getLogger(PasswordResetDAO.class);
    private static final int EXPIRACION_HORAS = 24;
    private UserDAO userDAO = new UserDAO();

    public void guardarToken(int idUsuario, String token) {
        String query = "INSERT INTO password_reset(id_usuario, fecha_hora_creacion, token, utilizado) VALUES (?, NOW(), ?, FALSE)";
        Connection conn = null;

        try {
            conn = ConnectionDB.getInstancia().getConn();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, idUsuario);
                stmt.setString(2, token);

                int affectedRows = stmt.executeUpdate();
                if (affectedRows > 0) {
                    logger.info("Token de recuperación guardado exitosamente para el usuario con ID {}", idUsuario);
                } else {
                    logger.warn("No se pudo guardar el token para el usuario con ID {}", idUsuario);
                }
            }
        } catch (SQLException e) {
            logger.error("Error al guardar token de recuperación para usuario con ID {}", idUsuario, e);
        } finally {
            ConnectionDB.getInstancia().releaseConn();
        }
    }

    public PasswordReset obtenerPorToken(String token) {
        String query = "SELECT idpassword_reset, id_usuario, fecha_hora_creacion, token, utilizado FROM password_reset WHERE token = ? AND utilizado = FALSE AND fecha_hora_creacion > ?";
        Connection conn = null;
        PasswordReset pr = null;

        try {
            conn = ConnectionDB.getInstancia().getConn();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                LocalDateTime threshold = LocalDateTime.now().minusHours(EXPIRACION_HORAS);
                stmt.setString(1, token);
                stmt.setTimestamp(2, Timestamp.valueOf(threshold));
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        pr = mappingPasswordReset(rs);
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error al obtener password reset con token: {}", token, e);
        } finally {
            ConnectionDB.getInstancia().releaseConn();
        }
        return pr;
    }

    public void marcarComoUtilizado(String token) {
        String query = "UPDATE password_reset SET utilizado = TRUE WHERE token = ?";
        Connection conn = null;

        try {
            conn = ConnectionDB.getInstancia().getConn();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, token);

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    logger.info("Token de recuperación marcado como utilizado: {}", token);
                } else {
                    logger.warn("No se encontró token para marcar como utilizado: {}", token);
                }
            }
        } catch (SQLException e) {
            logger.error("Error al marcar token como utilizado: {}", token, e);
        } finally {
            ConnectionDB.getInstancia().releaseConn();
        }
    }

    public void invalidarTokensPrevios(int idUsuario) {
        String query = "UPDATE password_reset SET utilizado = TRUE WHERE id_usuario = ? AND utilizado = FALSE";
        Connection conn = null;

        try {
            conn = ConnectionDB.getInstancia().getConn();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, idUsuario);

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    logger.info("Tokens previos invalidados para usuario con ID {}", idUsuario);
                }
            }
        } catch (SQLException e) {
            logger.error("Error al invalidar tokens previos para usuario con ID {}", idUsuario, e);
        } finally {
            ConnectionDB.getInstancia().releaseConn();
        }
    }

    public boolean existeTokenPendiente(int idUsuario) {
        String query = "SELECT COUNT(*) as cantidad FROM password_reset WHERE id_usuario = ? AND utilizado = FALSE AND fecha_hora_creacion > DATE_SUB(NOW(), INTERVAL ? HOUR)";
        Connection conn = null;
        boolean existe = false;

        try {
            conn = ConnectionDB.getInstancia().getConn();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, idUsuario);
                stmt.setInt(2, EXPIRACION_HORAS);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next() && rs.getInt("cantidad") > 0) {
                        existe = true;
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error al verificar tokens pendientes para usuario con ID {}", idUsuario, e);
        } finally {
            ConnectionDB.getInstancia().releaseConn();
        }
        return existe;
    }

    private PasswordReset mappingPasswordReset(ResultSet rs) throws SQLException {
        PasswordReset pr = new PasswordReset();
        pr.setId_password_reset(rs.getInt("idpassword_reset"));
        pr.setUsuario(userDAO.getById(rs.getInt("id_usuario")));
        pr.setFecha_hora_creacion(rs.getTimestamp("fecha_hora_creacion"));
        pr.setToken(rs.getString("token"));
        pr.setUtilizado(rs.getBoolean("utilizado"));
        return pr;
    }
}