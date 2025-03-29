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
        String query = "SELECT fecha_hora, id_usuario_calificado, puntuacion, observacion, id_viaje FROM feedback";
        
        try (
            Connection conn = ConnectionDB.getInstancia().getConn();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
        ) {
            while (rs.next()) {
                Feedback f = new Feedback();
                f.setFecha_hora(rs.getDate("fecha_hora"));
                f.setId_usuario_calificado(rs.getInt("id_usuario_calificado"));
                f.setObservacion(rs.getString("observacion"));
                f.setPuntuacion(rs.getInt("puntuacion"));
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
        String query = "SELECT fecha_hora, id_usuario_calificado, puntuacion, observacion, id_viaje FROM feedback WHERE id_usuario_calificado = ?";
        Connection conn = null;
        
        try {
            conn = ConnectionDB.getInstancia().getConn();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, u.getIdUsuario());
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Feedback f = new Feedback();
                        f.setFecha_hora(rs.getDate("fecha_hora"));
                        f.setId_usuario_calificado(rs.getInt("id_usuario_calificado"));
                        f.setObservacion(rs.getString("observacion"));
                        f.setPuntuacion(rs.getInt("puntuacion"));
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

    public void add(Feedback f) {
        String query = "INSERT INTO feedback(fecha_hora, id_usuario_calificado, puntuacion, observacion, id_viaje) VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        
        try {
            conn = ConnectionDB.getInstancia().getConn();
            try (PreparedStatement stmt = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
                stmt.setDate(1, f.getFecha_hora());
                stmt.setInt(2, f.getId_usuario_calificado());
                stmt.setInt(3, f.getPuntuacion());
                stmt.setString(4, f.getObservacion());
                stmt.setInt(5, f.getId_viaje());
                
                int affectedRows = stmt.executeUpdate();
                if (affectedRows > 0) {
                    logger.info("Feedback agregado exitosamente para el usuario con ID {}", f.getId_usuario_calificado());
                }
             }
            
        } catch (SQLException e) {
            logger.error("Error al agregar feedback para el usuario con ID {}", f.getId_usuario_calificado(), e);
        } finally {
            ConnectionDB.getInstancia().releaseConn();
        }
    }
}