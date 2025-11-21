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
        String query = "SELECT fecha_hora_feedback, id_usuario_calificado, puntuacion, observacion, id_reserva FROM feedback";

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
        String query = "SELECT fecha_hora_feedback, id_usuario_calificado, puntuacion, observacion, id_reserva FROM feedback WHERE id_usuario_calificado = ?";
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

    public LinkedList<Feedback> getByReserva(Reserva r) {
        LinkedList<Feedback> fs = new LinkedList<>();
        String query = "SELECT fecha_hora_feedback, id_usuario_calificado, puntuacion, observacion, id_reserva FROM feedback WHERE id_reserva = ?";
        Connection conn = null;

        try {
            conn = ConnectionDB.getInstancia().getConn();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, r.getIdReserva());
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Feedback f = mappingFeedback(rs);
                        fs.add(f);
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error al obtener feedbacks para la reserva con ID {}", r.getIdReserva(), e);
        } finally {
            ConnectionDB.getInstancia().releaseConn();
        }
        return fs;
    }

    public void add(Feedback f) {
        String query = "INSERT INTO feedback(id_usuario_calificado, id_reserva) VALUES (?, ?)";
        Connection conn = null;

        try {
            conn = ConnectionDB.getInstancia().getConn();
            try (PreparedStatement stmt = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, f.getUsuario_calificado().getIdUsuario());
                stmt.setInt(2, f.getReserva().getIdReserva());

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
        f.setObservacion(rs.getString("observacion"));
        f.setPuntuacion(rs.getInt("puntuacion"));
        f.setUsuario_calificado(userDAO.getById(rs.getInt("id_usuario_calificado")));
        f.setReserva(reservaDAO.getByReserva(rs.getInt("id_reserva")));
        return f;
    }
}