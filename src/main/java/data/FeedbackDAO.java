package data;

import data.exceptions.DataAccessException;
import entities.Feedback;
import entities.Reserva;
import entities.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class FeedbackDAO {

    private static final Logger logger = LoggerFactory.getLogger(FeedbackDAO.class);

    public LinkedList<Feedback> getAll() {
        logger.debug("Obteniendo todos los feedbacks");

        String query = "SELECT f.fecha_hora_feedback, f.id_usuario_calificado, f.puntuacion, f.id_reserva, " +
                "u.id_usuario, u.nombre, u.apellido, " +
                "r.id_reserva AS reserva_id, r.fecha_reserva, r.estado, r.id_viaje " +
                "FROM feedback f " +
                "INNER JOIN usuarios u ON f.id_usuario_calificado = u.id_usuario " +
                "INNER JOIN reservas r ON f.id_reserva = r.id_reserva";

        LinkedList<Feedback> feedbacks = new LinkedList<>();

        try (
                Connection conn = ConnectionDB.getInstancia().getConn();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)
        ) {
            while (rs.next()) {
                feedbacks.add(mappingFullFeedback(rs));
            }

            logger.info("Obtenidos {} feedbacks", feedbacks.size());
            return feedbacks;

        } catch (SQLException e) {
            logger.error("Error al obtener todos los feedbacks - Estado: {} - Código: {}",
                    e.getSQLState(), e.getErrorCode());
            throw new DataAccessException("Error al obtener todos los feedbacks", e);
        }
    }

    public Map<String, Object> getUserRating(Usuario u) {
        logger.debug("Obteniendo rating para usuario ID: {}", u.getIdUsuario());

        String query = "SELECT AVG(f.puntuacion) AS promedio, COUNT(f.puntuacion) AS cantidad " +
                "FROM feedback f WHERE f.id_usuario_calificado = ?";

        Map<String, Object> result = new HashMap<>();
        result.put("promedio", 0.0);
        result.put("cantidad", 0);

        try (
                Connection conn = ConnectionDB.getInstancia().getConn();
                PreparedStatement stmt = conn.prepareStatement(query)
        ) {
            stmt.setInt(1, u.getIdUsuario());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    double avg = rs.getDouble("promedio");
                    int count = rs.getInt("cantidad");

                    if (!rs.wasNull()) {
                        result.put("promedio", avg);
                    }
                    result.put("cantidad", count);

                    logger.debug("Rating obtenido para usuario ID {}: promedio={}, cantidad={}",
                            u.getIdUsuario(), avg, count);
                }
            }

            return result;

        } catch (SQLException e) {
            logger.error("Error al obtener rating para usuario ID {} - Estado: {} - Código: {}",
                    u.getIdUsuario(), e.getSQLState(), e.getErrorCode());
            throw new DataAccessException("Error al obtener rating del usuario", e);
        }
    }

    public Feedback getByReserva(Reserva r) {
        logger.debug("Obteniendo feedback para reserva ID: {}", r.getIdReserva());

        String query = "SELECT fecha_hora_feedback, id_usuario_calificado, puntuacion, id_reserva " +
                "FROM feedback WHERE id_reserva = ?";

        try (
                Connection conn = ConnectionDB.getInstancia().getConn();
                PreparedStatement stmt = conn.prepareStatement(query)
        ) {
            stmt.setInt(1, r.getIdReserva());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Feedback f = new Feedback();
                    return mappingFeedback(rs, f);
                } else {
                    logger.warn("No se encontró feedback para reserva ID: {}", r.getIdReserva());
                    return null;
                }
            }

        } catch (SQLException e) {
            logger.error("Error al obtener feedback para reserva ID {} - Estado: {} - Código: {}",
                    r.getIdReserva(), e.getSQLState(), e.getErrorCode());
            throw new DataAccessException("Error al obtener feedback por reserva", e);
        }
    }

    public void guardarFeedback(int puntuacion, int id_reserva) {
        logger.info("Guardando feedback con token: {}", id_reserva);

        String query = "UPDATE feedback SET fecha_hora_feedback=?, puntuacion=? WHERE id_reserva=?";

        try (
                Connection conn = ConnectionDB.getInstancia().getConn();
                PreparedStatement stmt = conn.prepareStatement(query)
        ) {
            stmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            stmt.setInt(2, puntuacion);
            stmt.setInt(3, id_reserva);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                logger.info("Feedback guardado exitosamente para token: {}", id_reserva);
            } else {
                logger.warn("No se encontró feedback con token: {}", id_reserva);
                throw new DataAccessException("No se encontró feedback con el token proporcionado");
            }

        } catch (SQLException e) {
            logger.error("Error al guardar feedback con ID reserva {} - Estado: {} - Código: {}",
                    id_reserva, e.getSQLState(), e.getErrorCode());
            throw new DataAccessException("Error al guardar feedback", e);
        }
    }

    public void addAll(List<Feedback> lista) {
        String query = "INSERT INTO feedback(id_usuario_calificado, id_reserva) VALUES (?, ?)";

        try (
                Connection conn = ConnectionDB.getInstancia().getConn();
                PreparedStatement stmt = conn.prepareStatement(query)
        ) {
            conn.setAutoCommit(false);

            for (Feedback f : lista) {
                stmt.setInt(1, f.getUsuario_calificado().getIdUsuario());
                stmt.setInt(2, f.getReserva().getIdReserva());
                stmt.addBatch();
            }

            stmt.executeBatch();
            conn.commit();

        } catch (SQLException e) {
            logger.error("Error al agregar feedback en batch: SQLState={} Code={}",
                    e.getSQLState(), e.getErrorCode());
            throw new DataAccessException("Error al agregar feedback", e);
        }
    }


    private Feedback mappingFeedback(ResultSet rs, Feedback f) throws SQLException {
        f.setFecha_hora(rs.getDate("fecha_hora_feedback"));
        f.setPuntuacion(rs.getInt("puntuacion"));
        return f;
    }

    private void mappingUser(ResultSet rs, Feedback f) throws SQLException {
        Usuario u = new Usuario();
        u.setIdUsuario(rs.getInt("id_usuario"));
        u.setNombre(rs.getString("nombre"));
        u.setApellido(rs.getString("apellido"));
        f.setUsuario_calificado(u);
    }

    private void mappingReserva(ResultSet rs, Feedback f) throws SQLException {
        Reserva r = new Reserva();
        r.setIdReserva(rs.getInt("reserva_id"));
        r.setFecha_reserva(rs.getDate("fecha_reserva").toString());
        r.setEstado(rs.getString("estado"));
        f.setReserva(r);
    }

    private Feedback mappingFullFeedback(ResultSet rs) throws SQLException {
        Feedback f = new Feedback();
        mappingFeedback(rs, f);
        mappingUser(rs, f);
        mappingReserva(rs, f);
        return f;
    }
}