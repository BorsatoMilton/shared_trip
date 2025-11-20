package data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import entidades.Rol;


public class RolDAO {
    private static final Logger logger = LoggerFactory.getLogger(RolDAO.class);

    public LinkedList<Rol> getAll() {

        String query = "SELECT id, nombre FROM roles";
        LinkedList<Rol> roles = new LinkedList<>();

        try (Connection conn = ConnectionDB.getInstancia().getConn();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                roles.add(mapearRol(rs));
            }

            logger.debug("Obtenidos {} roles", roles.size());

        } catch (SQLException e) {
            String errorMsg = "Error al obtener roles";
            logger.error(errorMsg, e);
            throw new DAOException(errorMsg, e);
        }
        return roles;
    }

    public Rol getById(int idRol) {
        String query = "SELECT id, nombre FROM roles WHERE id = ?";

        try (Connection conn = ConnectionDB.getInstancia().getConn();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idRol);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearRol(rs);
                }
                logger.info("No se encontró rol con ID: {}", idRol);
                return null;
            }

        } catch (SQLException e) {
            final String errorMsg = String.format("Error al obtener rol ID: %d - %s",
                    idRol, e.getMessage());
            logger.error(errorMsg);
            throw new DAOException(errorMsg, e);
        }
    }

    private Rol mapearRol(ResultSet rs) throws SQLException {
        Rol rol = new Rol();
        rol.setIdRol(rs.getInt("id"));
        rol.setNombre(rs.getString("nombre"));
        return rol;
    }


    public static class DAOException extends RuntimeException {
        public DAOException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}