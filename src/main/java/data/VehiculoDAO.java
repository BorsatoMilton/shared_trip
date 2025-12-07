package data;

import data.exceptions.DataAccessException;
import entities.Usuario;
import entities.Vehiculo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.LinkedList;

public class VehiculoDAO {
    private static final Logger logger = LoggerFactory.getLogger(VehiculoDAO.class);

    public LinkedList<Vehiculo> getAll() {
        logger.debug("Obteniendo todos los vehículos");
        LinkedList<Vehiculo> vehiculos = new LinkedList<>();

        String sql =
                "SELECT v.id_vehiculo, v.patente, v.modelo, v.anio, u.id_usuario, " +
                        "u.nombre, u.apellido, u.correo " +
                        "FROM vehiculos v " +
                        "INNER JOIN usuarios u ON u.id_usuario = v.usuario_duenio_id " +
                        "WHERE u.fecha_baja IS NULL AND v.activo = TRUE";

        try (Connection conn = ConnectionDB.getInstancia().getConn();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {

                Usuario propietario = new Usuario();
                propietario.setIdUsuario(rs.getInt("id_usuario"));
                propietario.setNombre(rs.getString("nombre"));
                propietario.setApellido(rs.getString("apellido"));
                propietario.setCorreo(rs.getString("correo"));

                Vehiculo v = new Vehiculo();
                v.setId_vehiculo(rs.getInt("id_vehiculo"));
                v.setPatente(rs.getString("patente"));
                v.setModelo(rs.getString("modelo"));
                v.setAnio(rs.getInt("anio"));
                v.setPropietario(propietario);

                vehiculos.add(v);
            }

            logger.info("Obtenidos {} vehículos", vehiculos.size());
            return vehiculos;

        } catch (SQLException e) {
            logger.error("Error al obtener todos los vehículos", e);
            throw new DataAccessException("Error al obtener todos los vehículos", e);
        }
    }

    public Vehiculo getById_vehiculo(int id_vehiculo) {
        logger.debug("Buscando vehículo con ID: {}", id_vehiculo);

        String sql =
                "SELECT id_vehiculo, patente, modelo, anio, usuario_duenio_id " +
                        "FROM vehiculos WHERE id_vehiculo = ? AND activo = TRUE";

        try (Connection conn = ConnectionDB.getInstancia().getConn();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id_vehiculo);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {

                    Usuario propietario = new Usuario();
                    propietario.setIdUsuario(rs.getInt("usuario_duenio_id"));

                    Vehiculo v = new Vehiculo();
                    v.setId_vehiculo(rs.getInt("id_vehiculo"));
                    v.setPatente(rs.getString("patente"));
                    v.setModelo(rs.getString("modelo"));
                    v.setAnio(rs.getInt("anio"));
                    v.setPropietario(propietario);


                    logger.debug("Vehículo encontrado: {} (ID: {})", v.getPatente(), id_vehiculo);
                    return v;
                }
            }

            logger.warn("Vehículo no encontrado con ID: {}", id_vehiculo);
            return null;

        } catch (SQLException e) {
            logger.error("Error al obtener vehículo por ID {}", id_vehiculo, e);
            throw new DataAccessException("Error al obtener vehículo por ID", e);
        }
    }

    public Vehiculo getByPatente(String patente) {
        logger.debug("Buscando vehículo con patente: {}", patente);

        String sql =
                "SELECT id_vehiculo, patente, modelo, anio, usuario_duenio_id " +
                        "FROM vehiculos WHERE patente = ? AND activo = TRUE";

        try (Connection conn = ConnectionDB.getInstancia().getConn();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, patente);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Usuario propietario = new Usuario();
                    propietario.setIdUsuario(rs.getInt("usuario_duenio_id"));

                    Vehiculo v = new Vehiculo();
                    v.setId_vehiculo(rs.getInt("id_vehiculo"));
                    v.setPatente(rs.getString("patente"));
                    v.setModelo(rs.getString("modelo"));
                    v.setAnio(rs.getInt("anio"));
                    v.setPropietario(propietario);
                    logger.debug("Vehículo encontrado: {}", patente);
                    return v;
                }
            }

            logger.warn("Vehículo no encontrado con patente: {}", patente);
            return null;

        } catch (SQLException e) {
            logger.error("Error al obtener vehículo patente {}", patente, e);
            throw new DataAccessException("Error al obtener vehículo por patente", e);
        }
    }

    public void altaVehiculo(Vehiculo v, int idUsuario) {
        logger.info("Creando nuevo vehículo: {}", v.getPatente());

        String sql =
                "INSERT INTO vehiculos(patente, modelo, anio, usuario_duenio_id) " +
                        "VALUES(?,?,?,?)";

        try (Connection conn = ConnectionDB.getInstancia().getConn();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, v.getPatente());
            stmt.setString(2, v.getModelo());
            stmt.setInt(3, v.getAnio());
            stmt.setInt(4, idUsuario);

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        v.setId_vehiculo(keys.getInt(1));
                        logger.info("Vehículo creado exitosamente (ID: {})", v.getId_vehiculo());
                    }
                }
            } else {
                logger.warn("No se pudo crear vehículo {}", v.getPatente());
            }

        } catch (SQLException e) {
            logger.error("Error al crear vehículo {}", v.getPatente(), e);
            throw new DataAccessException("Error al crear vehículo", e);
        }
    }

    public void update(Vehiculo v, int id_vehiculo) {
        logger.info("Actualizando vehículo ID: {}", id_vehiculo);

        String sql =
                "UPDATE vehiculos SET patente = ?, modelo = ?, anio = ? " +
                        "WHERE id_vehiculo = ? AND activo = TRUE";

        try (Connection conn = ConnectionDB.getInstancia().getConn();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, v.getPatente());
            stmt.setString(2, v.getModelo());
            stmt.setInt(3, v.getAnio());
            stmt.setInt(4, id_vehiculo);

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                logger.info("Vehículo actualizado (ID: {})", id_vehiculo);
            } else {
                logger.warn("No se encontró vehículo ID {}", id_vehiculo);
            }

        } catch (SQLException e) {
            logger.error("Error al actualizar vehículo ID {}", id_vehiculo, e);
            throw new DataAccessException("Error al actualizar vehículo", e);
        }
    }

    public LinkedList<Vehiculo> getByUser(Usuario u) {
        logger.debug("Obteniendo vehículos del usuario ID: {}", u.getIdUsuario());

        LinkedList<Vehiculo> lista = new LinkedList<>();

        String sql =
                "SELECT id_vehiculo, patente, modelo, anio, usuario_duenio_id " +
                        "FROM vehiculos WHERE usuario_duenio_id = ? AND activo = TRUE";

        try (Connection conn = ConnectionDB.getInstancia().getConn();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, u.getIdUsuario());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Usuario propietario = new Usuario();
                    propietario.setIdUsuario(rs.getInt("usuario_duenio_id"));

                    Vehiculo v = new Vehiculo();
                    v.setId_vehiculo(rs.getInt("id_vehiculo"));
                    v.setPatente(rs.getString("patente"));
                    v.setModelo(rs.getString("modelo"));
                    v.setAnio(rs.getInt("anio"));
                    v.setPropietario(propietario);
                    lista.add(v);
                }
            }

            logger.debug("Encontrados {} vehículos para usuario ID {}", lista.size(), u.getIdUsuario());
            return lista;

        } catch (SQLException e) {
            logger.error("Error al obtener vehículos del usuario {}", u.getIdUsuario(), e);
            throw new DataAccessException("Error al obtener vehículos del usuario", e);
        }
    }

    public boolean eliminarVehiculo(int id_vehiculo) {
        logger.info("Eliminando vehículo ID: {}", id_vehiculo);

        String sql = "UPDATE vehiculos SET activo = FALSE WHERE id_vehiculo = ?";

        try (Connection conn = ConnectionDB.getInstancia().getConn();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id_vehiculo);

            int rows = stmt.executeUpdate();
            boolean eliminado = rows > 0;

            if (eliminado) {
                logger.info("Vehículo eliminado ID {}", id_vehiculo);
            } else {
                logger.warn("No se encontró vehículo ID {}", id_vehiculo);
            }

            return eliminado;

        } catch (SQLException e) {
            logger.error("Error al eliminar vehículo ID {}", id_vehiculo, e);
            throw new DataAccessException("Error al eliminar vehículo", e);
        }
    }

    public boolean tieneViajes(int idVehiculo) {
        logger.debug("Verificando si vehículo ID {} tiene viajes asociados", idVehiculo);

        String sql = "SELECT COUNT(*) AS total FROM viajes WHERE id_vehiculo_viaje = ? AND activo = TRUE";

        try (Connection conn = ConnectionDB.getInstancia().getConn();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idVehiculo);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt("total") > 0;
            }

        } catch (SQLException e) {
            logger.error("Error al verificar viajes del vehículo ID {}", idVehiculo, e);
            throw new DataAccessException("Error al verificar viajes del vehículo", e);
        }
    }
}
