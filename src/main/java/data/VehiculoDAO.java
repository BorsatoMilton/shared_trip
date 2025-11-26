package data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;

import entidades.Usuario;
import entidades.Vehiculo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VehiculoDAO {
	private static final Logger logger = LoggerFactory.getLogger(VehiculoDAO.class);

	public LinkedList<Vehiculo> getAll() {
        LinkedList<Vehiculo> vehiculos = new LinkedList<>();
        logger.debug("Obteniendo todos los vehículos");

        try (Connection conn = ConnectionDB.getInstancia().getConn();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT id_vehiculo, patente, modelo, anio, usuario_duenio_id " +
                 "FROM vehiculos " +
                 "INNER JOIN usuarios ON usuarios.id_usuario = vehiculos.usuario_duenio_id " +
                 "WHERE usuarios.fecha_baja IS NULL")) {

            while (rs.next()) {
                Vehiculo v = new Vehiculo();
                v.setId_vehiculo(rs.getInt("id_vehiculo"));
                v.setPatente(rs.getString("patente"));
                v.setModelo(rs.getString("modelo"));
                v.setAnio(rs.getInt("anio"));
                v.setUsuario_duenio_id(rs.getInt("usuario_duenio_id"));
                vehiculos.add(v);
            }
            
            logger.info("Obtenidos {} vehículos", vehiculos.size());

        } catch (SQLException e) {
            logger.error("Error al obtener todos los vehículos - Estado: {} - Código: {}", 
                        e.getSQLState(), e.getErrorCode(), e);
        } finally {
            ConnectionDB.getInstancia().releaseConn();
        }

        return vehiculos;
    }

	public Vehiculo getById_vehiculo(int id_vehiculo) {
        logger.debug("Buscando vehículo con ID: {}", id_vehiculo);
        Vehiculo v = null;
        
        String query = "SELECT id_vehiculo, patente, modelo, anio, usuario_duenio_id " +
                      "FROM vehiculos WHERE id_vehiculo = ?";

        try (Connection conn = ConnectionDB.getInstancia().getConn();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, id_vehiculo);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    v = new Vehiculo();
                    v.setId_vehiculo(rs.getInt("id_vehiculo"));
                    v.setPatente(rs.getString("patente"));
                    v.setModelo(rs.getString("modelo"));
                    v.setAnio(rs.getInt("anio"));
                    v.setUsuario_duenio_id(rs.getInt("usuario_duenio_id"));
                    logger.debug("Vehículo encontrado: {} (ID: {})", v.getPatente(), id_vehiculo);
                } else {
                    logger.warn("Vehículo no encontrado con ID: {}", id_vehiculo);
                }
            }
        } catch (SQLException e) {
            logger.error("Error al obtener vehículo ID {} - Estado: {} - Código: {}", 
                        id_vehiculo, e.getSQLState(), e.getErrorCode(), e);
        } finally {
            ConnectionDB.getInstancia().releaseConn();
        }

        return v;
    }

	public Vehiculo getByPatente(String patente) {
        logger.debug("Buscando vehículo con patente: {}", patente);
        Vehiculo v = null;
        
        String query = "SELECT id_vehiculo, patente, modelo, anio, usuario_duenio_id " +
                      "FROM vehiculos WHERE patente = ?";

        try (Connection conn = ConnectionDB.getInstancia().getConn();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, patente);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    v = new Vehiculo();
                    v.setId_vehiculo(rs.getInt("id_vehiculo"));
                    v.setPatente(rs.getString("patente"));
                    v.setModelo(rs.getString("modelo"));
                    v.setAnio(rs.getInt("anio"));
                    v.setUsuario_duenio_id(rs.getInt("usuario_duenio_id"));
                    logger.debug("Vehículo encontrado: {}", patente);
                } else {
                    logger.warn("Vehículo no encontrado con patente: {}", patente);
                }
            }
        } catch (SQLException e) {
            logger.error("Error al obtener vehículo patente {} - Estado: {} - Código: {}", 
                        patente, e.getSQLState(), e.getErrorCode(), e);
        } finally {
            ConnectionDB.getInstancia().releaseConn();
        }

        return v;
    }


	public void altaVehiculo(Vehiculo v) {
        logger.info("Creando nuevo vehículo: {}", v.getPatente());
        
        String query = "INSERT INTO vehiculos(patente, modelo, anio, usuario_duenio_id) VALUES(?,?,?,?)";

        try (Connection conn = ConnectionDB.getInstancia().getConn();
             PreparedStatement stmt = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, v.getPatente());
            stmt.setString(2, v.getModelo());
            stmt.setInt(3, v.getAnio());
            stmt.setInt(4, v.getUsuario_duenio_id());

            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet keyResultSet = stmt.getGeneratedKeys()) {
                    if (keyResultSet != null && keyResultSet.next()) {
                        v.setId_vehiculo(keyResultSet.getInt(1));
                        logger.info("Vehículo creado exitosamente: {} (ID: {})", v.getPatente(), v.getId_vehiculo());
                    }
                }
            } else {
                logger.error("No se pudo crear el vehículo: {}", v.getPatente());
            }

        } catch (SQLException e) {
            logger.error("Error al crear vehículo {} - Estado: {} - Código: {}", 
                        v.getPatente(), e.getSQLState(), e.getErrorCode(), e);
        } finally {
            ConnectionDB.getInstancia().releaseConn();
        }
    }


	public void update(Vehiculo v, int id_vehiculo) {
        logger.info("Actualizando vehículo ID: {}", id_vehiculo);
        
        String query = "UPDATE vehiculos SET patente = ?, modelo = ?, anio = ? WHERE id_vehiculo = ?";

        try (Connection conn = ConnectionDB.getInstancia().getConn();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, v.getPatente());
            stmt.setString(2, v.getModelo());
            stmt.setInt(3, v.getAnio());
            stmt.setInt(4, id_vehiculo);

            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                logger.info("Vehículo actualizado exitosamente: {} (ID: {})", v.getPatente(), id_vehiculo);
            } else {
                logger.warn("No se encontró vehículo con ID: {}", id_vehiculo);
            }

        } catch (SQLException e) {
            logger.error("Error al actualizar vehículo ID {} - Estado: {} - Código: {}", 
                        id_vehiculo, e.getSQLState(), e.getErrorCode(), e);
        } finally {
            ConnectionDB.getInstancia().releaseConn();
        }
    }

	public LinkedList<Vehiculo> getByUser(Usuario u) {
        logger.debug("Obteniendo vehículos del usuario ID: {}", u.getIdUsuario());
        LinkedList<Vehiculo> vehiculos = new LinkedList<>();
        
        String query = "SELECT id_vehiculo, patente, modelo, anio, usuario_duenio_id " +
                      "FROM vehiculos WHERE usuario_duenio_id = ?";

        try (Connection conn = ConnectionDB.getInstancia().getConn();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, u.getIdUsuario());
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Vehiculo vehiculo = new Vehiculo();
                    vehiculo.setId_vehiculo(rs.getInt("id_vehiculo"));
                    vehiculo.setPatente(rs.getString("patente"));
                    vehiculo.setModelo(rs.getString("modelo"));
                    vehiculo.setAnio(rs.getInt("anio"));
                    vehiculo.setUsuario_duenio_id(rs.getInt("usuario_duenio_id"));
                    vehiculos.add(vehiculo);
                }
            }
            
            logger.debug("Encontrados {} vehículos para usuario ID: {}", vehiculos.size(), u.getIdUsuario());

        } catch (SQLException e) {
            logger.error("Error al obtener vehículos del usuario ID {} - Estado: {} - Código: {}", 
                        u.getIdUsuario(), e.getSQLState(), e.getErrorCode(), e);
        } finally {
            ConnectionDB.getInstancia().releaseConn();
        }

        return vehiculos;
    }

//    public void delete(Vehiculo v) {
//
//        PreparedStatement stmt = null;
//        try {
//            stmt = ConnectionDB.getInstancia().getConn()
//                    .prepareStatement("delete * from vehiculos where id_vehiculo=?");
//            stmt.setInt(1, v.getId_vehiculo());
//            int rowsAffected = stmt.executeUpdate();
//
//            if (rowsAffected > 1) {
//                System.out.println("Se ha borrado el vehiculo con la patente: " + v.getPatente());
//            } else {
//                System.out.println("No se ha encontrado ningún vehiculo");
//            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (stmt != null) {
//                    stmt.close();
//                }
//                ConnectionDB.getInstancia().releaseConn();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    public boolean eliminarVehiculo(int id_vehiculo) {
    	logger.info("Eliminando vehículo ID: {}", id_vehiculo);
        
        String query = "DELETE FROM vehiculos WHERE id_vehiculo = ?";

        try (Connection conn = ConnectionDB.getInstancia().getConn();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, id_vehiculo);
            int rowsAffected = stmt.executeUpdate();
            
            boolean eliminado = rowsAffected > 0;
            if (eliminado) {
                logger.info("Vehículo eliminado exitosamente: ID {}", id_vehiculo);
            } else {
                logger.warn("No se encontró vehículo con ID: {}", id_vehiculo);
            }
            
            return eliminado;

        } catch (SQLException e) {
            logger.error("Error al eliminar vehículo ID {} - Estado: {} - Código: {}", 
                        id_vehiculo, e.getSQLState(), e.getErrorCode(), e);
            return false;
        } finally {
            ConnectionDB.getInstancia().releaseConn();
        }
    }

    public boolean tieneViajes(int idVehiculo) {
        logger.debug("Verificando si vehículo ID {} tiene viajes asociados", idVehiculo);
        
        String sql = "SELECT COUNT(*) AS total FROM viajes WHERE id_vehiculo_viaje = ?";

        try (Connection conn = ConnectionDB.getInstancia().getConn();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idVehiculo);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    boolean tieneViajes = rs.getInt("total") > 0;
                    logger.debug("Vehículo ID {} - Tiene viajes: {}", idVehiculo, tieneViajes);
                    return tieneViajes;
                }
            }

        } catch (SQLException e) {
            logger.error("Error al verificar viajes del vehículo ID {} - Estado: {} - Código: {}", 
                        idVehiculo, e.getSQLState(), e.getErrorCode(), e);
        } finally {
            ConnectionDB.getInstancia().releaseConn();
        }

        return false;
    }


}
