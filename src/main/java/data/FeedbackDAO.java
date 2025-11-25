package data;

import java.sql.*;
import java.util.LinkedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import entidades.*;

public class FeedbackDAO {

    private static final Logger logger = LoggerFactory.getLogger(FeedbackDAO.class);

    public LinkedList<Feedback> getAll() {
        LinkedList<Feedback> feedbacks = new LinkedList<>();
        String query = "SELECT f.fecha_hora_feedback, f.id_usuario_calificado, f.puntuacion, f.id_reserva, f.token, u.id_usuario, u.nombre, u.apellido, r.id_reserva AS reserva_id, r.fecha_reserva, r.estado, r.id_viaje FROM feedback f INNER JOIN usuarios u ON f.id_usuario_calificado = u.id_usuario INNER JOIN reservas r ON f.id_reserva = r.id_reserva";

        try (
                Connection conn = ConnectionDB.getInstancia().getConn();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)
        ) {
            while (rs.next()) {
                Feedback f = mappingFullFeedback(rs);
                feedbacks.add(f);
            }
        } catch (SQLException e) {
            logger.error("Error al obtener todos los feedbacks", e);
        } finally {
            ConnectionDB.getInstancia().releaseConn();
        }
        return feedbacks;
    }

    public LinkedList<Feedback> getByUser(Usuario u) {
        LinkedList<Feedback> fs = new LinkedList<>();
        String query = "SELECT f.fecha_hora_feedback, f.id_usuario_calificado, f.puntuacion, f.id_reserva, f.token, u.id_usuario, u.nombre, u.apellido, r.id_reserva AS reserva_id, r.fecha_reserva, r.estado, r.id_viaje FROM feedback f INNER JOIN usuarios u ON f.id_usuario_calificado = u.id_usuario INNER JOIN reservas r ON f.id_reserva = r.id_reserva WHERE f.id_usuario_calificado = ?";
        Connection conn = null;

        try {
            conn = ConnectionDB.getInstancia().getConn();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, u.getIdUsuario());
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Feedback f = mappingFullFeedback(rs);
                        fs.add(f);
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error al obtener feedbacks para el usuario con ID {}", u.getIdUsuario(), e);
        } finally {
            ConnectionDB.getInstancia().releaseConn();
        }
        return fs;
    }


    public Feedback getByReserva(Reserva r) {
        String query = "SELECT fecha_hora_feedback, id_usuario_calificado, puntuacion, id_reserva, token FROM feedback WHERE id_reserva = ?";
        Connection conn = null;
        Feedback f = null;
        try {
            conn = ConnectionDB.getInstancia().getConn();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, r.getIdReserva());
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        f = new Feedback();
                        f = mappingFeedback(rs,f);
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error al obtener feedbacks para la reserva con ID {}", r.getIdReserva(), e);
        } finally {
            ConnectionDB.getInstancia().releaseConn();
        }
        return f;
    }

    public void guardarFeedback(int puntuacion, String token) {
        String query = "UPDATE feedback SET fecha_hora_feedback=?, puntuacion=? WHERE token=?";
        Connection conn = null;

        try {
            conn = ConnectionDB.getInstancia().getConn();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                java.sql.Timestamp now = new java.sql.Timestamp(System.currentTimeMillis());
                stmt.setTimestamp(1, now);
                stmt.setInt(2, puntuacion);
                stmt.setString(3, token);

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    logger.info("Feedback para la reserva con token: {} otorgado exitosamente", token);
                } else {
                    logger.warn("No se encontró feedback con token: {}", token);
                }
            }
        } catch (SQLException e) {
            logger.error("Error al actualizar feedback con token: {}", token, e);
        } finally {
            ConnectionDB.getInstancia().releaseConn();
        }
    }

    public void add(Feedback f) {
        String query = "INSERT INTO feedback(id_usuario_calificado, id_reserva, token) VALUES (?, ?, ?)";
        Connection conn = null;

        try {
            conn = ConnectionDB.getInstancia().getConn();
            try (PreparedStatement stmt = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, f.getUsuario_calificado().getIdUsuario());
                stmt.setInt(2, f.getReserva().getIdReserva());
                stmt.setString(3, f.getToken());

                int affectedRows = stmt.executeUpdate();
                if (affectedRows > 0) {
                    logger.info("Feedback agregado exitosamente para el usuario con ID {}", f.getUsuario_calificado().getIdUsuario());
                }
            }

        } catch (SQLException e) {
            logger.error("Error al agregar feedback para el usuario con ID {}", f.getUsuario_calificado().getIdUsuario(), e);
        } finally {
            ConnectionDB.getInstancia().releaseConn();
        }
    }


    private Feedback mappingFeedback(ResultSet rs, Feedback f) throws SQLException {
        f.setFecha_hora(rs.getDate("fecha_hora_feedback"));
        f.setPuntuacion(rs.getInt("puntuacion"));
        f.setToken(rs.getString("token"));
        return f;
    }


    private Feedback mappingUser(ResultSet rs, Feedback f) throws SQLException {
        Usuario u = new Usuario();
        u.setIdUsuario(rs.getInt("user_id"));
        u.setNombre(rs.getString("user_nombre"));
        u.setApellido(rs.getString("user_apellido"));
        f.setUsuario_calificado(u);
        return f;
    }

    private Feedback mappingBooking(ResultSet rs, Feedback f) throws SQLException {
        Reserva r = new Reserva();
        r.setIdReserva(rs.getInt("reserva_id"));
        r.setFecha_reserva(rs.getDate("fecha_reserva").toString());
        r.setEstado(rs.getString("estado"));
        f.setReserva(r);
        return f;
    }

    private Feedback mappingFullFeedback(ResultSet rs) throws SQLException {
        Feedback f = new Feedback();
        mappingFeedback(rs, f);
        mappingUser(rs, f);
        mappingBooking(rs,f);
        return f;
    }
}