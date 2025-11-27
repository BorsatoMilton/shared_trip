package data;

import java.sql.*;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import entidades.*;
import data.exceptions.DataAccessException;

public class PasswordResetDAO {

    private static final Logger logger = LoggerFactory.getLogger(PasswordResetDAO.class);
    private static final int EXPIRACION_HORAS = 24;
    private UserDAO userDAO = new UserDAO();

    public void guardarToken(int idUsuario, String token) {
        logger.info("Guardando token de recuperación para usuario ID: {}", idUsuario);

        String query = "INSERT INTO password_reset(id_usuario, fecha_hora_creacion, token, utilizado) VALUES (?, NOW(), ?, FALSE)";

        try {
            Connection conn = ConnectionDB.getInstancia().getConn();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, idUsuario);
                stmt.setString(2, token);

                int affectedRows = stmt.executeUpdate();
                if (affectedRows > 0) {
                    logger.info("Token de recuperación guardado exitosamente para usuario ID: {}", idUsuario);
                } else {
                    logger.warn("No se pudo guardar el token para usuario ID: {}", idUsuario);
                    throw new DataAccessException("No se pudo guardar el token de recuperación");
                }
            }
        } catch (SQLException e) {
            logger.error("Error al guardar token de recuperación para usuario ID {} - Estado: {} - Código: {}",
                    idUsuario, e.getSQLState(), e.getErrorCode(), e);
            throw new DataAccessException("Error al guardar token de recuperación", e);
        } finally {
            ConnectionDB.getInstancia().releaseConn();
        }
    }

    public PasswordReset obtenerPorToken(String token) {
        logger.debug("Buscando password reset con token: {}", token);
        PasswordReset pr = null;

        String query = "SELECT idpassword_reset, id_usuario, fecha_hora_creacion, token, utilizado " +
                "FROM password_reset WHERE token = ? AND utilizado = FALSE AND fecha_hora_creacion > ?";

        try {
            Connection conn = ConnectionDB.getInstancia().getConn();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                LocalDateTime threshold = LocalDateTime.now().minusHours(EXPIRACION_HORAS);
                stmt.setString(1, token);
                stmt.setTimestamp(2, Timestamp.valueOf(threshold));

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        pr = mappingPasswordReset(rs);
                        logger.debug("Token encontrado para recuperación de contraseña");
                    } else {
                        logger.warn("Token no encontrado o expirado: {}", token);
                    }
                }
            }
            return pr;

        } catch (SQLException e) {
            logger.error("Error al obtener password reset con token {} - Estado: {} - Código: {}",
                    token, e.getSQLState(), e.getErrorCode(), e);
            throw new DataAccessException("Error al obtener token de recuperación", e);
        } finally {
            ConnectionDB.getInstancia().releaseConn();
        }
    }

    public void marcarComoUtilizado(String token) {
        logger.info("Marcando token como utilizado: {}", token);

        String query = "UPDATE password_reset SET utilizado = TRUE WHERE token = ?";

        try {
            Connection conn = ConnectionDB.getInstancia().getConn();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, token);

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    logger.info("Token de recuperación marcado como utilizado: {}", token);
                } else {
                    logger.warn("No se encontró token para marcar como utilizado: {}", token);
                    throw new DataAccessException("Token no encontrado para marcar como utilizado");
                }
            }

        } catch (SQLException e) {
            logger.error("Error al marcar token como utilizado {} - Estado: {} - Código: {}",
                    token, e.getSQLState(), e.getErrorCode(), e);
            throw new DataAccessException("Error al marcar token como utilizado", e);
        } finally {
            ConnectionDB.getInstancia().releaseConn();
        }
    }

    public void invalidarTokensPrevios(int idUsuario) {
        logger.info("Invalidando tokens previos para usuario ID: {}", idUsuario);

        String query = "UPDATE password_reset SET utilizado = TRUE WHERE id_usuario = ? AND utilizado = FALSE";

        try {
            Connection conn = ConnectionDB.getInstancia().getConn();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, idUsuario);

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    logger.info("Tokens previos invalidados para usuario ID: {}", idUsuario);
                } else {
                    logger.debug("No había tokens previos para invalidar - Usuario ID: {}", idUsuario);
                }
            }

        } catch (SQLException e) {
            logger.error("Error al invalidar tokens previos para usuario ID {} - Estado: {} - Código: {}",
                    idUsuario, e.getSQLState(), e.getErrorCode(), e);
            throw new DataAccessException("Error al invalidar tokens previos", e);
        } finally {
            ConnectionDB.getInstancia().releaseConn();
        }
    }

    public boolean existeTokenPendiente(int idUsuario) {
        logger.debug("Verificando si existen tokens pendientes para usuario ID: {}", idUsuario);

        String query = "SELECT COUNT(*) as cantidad FROM password_reset " +
                "WHERE id_usuario = ? AND utilizado = FALSE " +
                "AND fecha_hora_creacion > DATE_SUB(NOW(), INTERVAL ? HOUR)";

        try {
            Connection conn = ConnectionDB.getInstancia().getConn();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, idUsuario);
                stmt.setInt(2, EXPIRACION_HORAS);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        boolean existe = rs.getInt("cantidad") > 0;
                        logger.debug("Usuario ID {} - Tiene tokens pendientes: {}", idUsuario, existe);
                        return existe;
                    }
                }
                return false;
            }

        } catch (SQLException e) {
            logger.error("Error al verificar tokens pendientes para usuario ID {} - Estado: {} - Código: {}",
                    idUsuario, e.getSQLState(), e.getErrorCode(), e);
            throw new DataAccessException("Error al verificar tokens pendientes", e);
        } finally {
            ConnectionDB.getInstancia().releaseConn();
        }
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