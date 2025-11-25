package data;

import java.sql.*;
import java.util.LinkedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import entidades.*;

public class FeedbackDAO {

    private static final Logger logger = LoggerFactory.getLogger(FeedbackDAO.class);
    private UserDAO userDAO = new UserDAO();
    private ReservaDAO reservaDAO = new ReservaDAO();

    public LinkedList<Feedback> getAll() {
        LinkedList<Feedback> feedbacks = new LinkedList<>();
        String query = "SELECT fecha_hora_feedback, id_usuario_calificado, puntuacion, id_reserva, token FROM feedback";

        try (
                Connection conn = ConnectionDB.getInstancia().getConn();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)
        ) {
            while (rs.next()) {
                Feedback f = mappingFeedback(rs);
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
        String query = "SELECT fecha_hora_feedback, id_usuario_calificado, puntuacion, id_reserva, token FROM feedback WHERE id_usuario_calificado = ?";
        Connection conn = null;

        try {
            conn = ConnectionDB.getInstancia().getConn();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, u.getIdUsuario());
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Feedback f = mappingFeedback(rs);
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
                        f = mappingFeedback(rs);
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

    private Feedback mappingFeedback(ResultSet rs) throws SQLException { // CHEQUEAR Y CAMBIAR LOS QUERY A INNER JOIN
        Feedback f = new Feedback();
        f.setFecha_hora(rs.getDate("fecha_hora_feedback"));
        f.setPuntuacion(rs.getInt("puntuacion"));
        f.setToken(rs.getString("token"));
        f.setUsuario_calificado(userDAO.getById(rs.getInt("id_usuario_calificado")));
        f.setReserva(reservaDAO.getByReserva(rs.getInt("id_reserva")));
        return f;
    }
}