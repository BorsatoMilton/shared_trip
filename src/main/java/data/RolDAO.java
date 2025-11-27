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
import utils.DataAccessException;


public class RolDAO {
    private static final Logger logger = LoggerFactory.getLogger(RolDAO.class);

    public LinkedList<Rol> getAll() {
        logger.debug("Obteniendo todos los roles");

        String query = "SELECT id, nombre FROM roles";
        LinkedList<Rol> roles = new LinkedList<>();

        try {
            Connection conn = ConnectionDB.getInstancia().getConn();
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {

                while (rs.next()) {
                    roles.add(mapearRol(rs));
                }

                logger.info("Obtenidos {} roles", roles.size());
            }
            return roles;

        } catch (SQLException e) {
            logger.error("Error al obtener roles - Estado: {} - Código: {}",
                    e.getSQLState(), e.getErrorCode(), e);
            throw new DataAccessException("Error al obtener roles", e);
        } finally {
            ConnectionDB.getInstancia().releaseConn();
        }
    }

    public Rol getById(int idRol) {
        logger.debug("Buscando rol con ID: {}", idRol);

        String query = "SELECT id, nombre FROM roles WHERE id = ?";

        try {
            Connection conn = ConnectionDB.getInstancia().getConn();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setInt(1, idRol);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        logger.debug("Rol encontrado: ID {}", idRol);
                        return mapearRol(rs);
                    }
                    logger.warn("Rol no encontrado con ID: {}", idRol);
                    return null;
                }
            }

        } catch (SQLException e) {
            logger.error("Error al obtener rol ID {} - Estado: {} - Código: {}",
                    idRol, e.getSQLState(), e.getErrorCode(), e);
            throw new DataAccessException("Error al obtener rol por ID", e);
        } finally {
            ConnectionDB.getInstancia().releaseConn();
        }
    }

    private Rol mapearRol(ResultSet rs) throws SQLException {
        Rol rol = new Rol();
        rol.setIdRol(rs.getInt("id"));
        rol.setNombre(rs.getString("nombre"));
        return rol;
    }
}