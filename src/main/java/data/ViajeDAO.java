package data;

import java.sql.*;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.Map;

import entidades.Vehiculo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import entidades.Usuario;
import entidades.Viaje;
import utils.DataAccessException;

public class ViajeDAO {
    private FeedbackDAO fDAO = new FeedbackDAO();
    private static final Logger logger = LoggerFactory.getLogger(ViajeDAO.class);

    public LinkedList<Viaje> getAll() {
        logger.debug("Obteniendo todos los viajes activos con detalles");
        LinkedList<Viaje> viajes = new LinkedList<>();

        String query = "SELECT " +
                "v.*, " +
                "u.id_usuario as conductor_id, u.nombre as conductor_nombre, " +
                "u.apellido as conductor_apellido, u.correo as conductor_correo, " +
                "u.telefono as conductor_telefono, " +
                "veh.id_vehiculo, veh.patente, veh.modelo, veh.anio " +
                "FROM viajes v " +
                "INNER JOIN usuarios u ON u.id_usuario = v.id_conductor " +
                "INNER JOIN vehiculos veh ON veh.id_vehiculo = v.id_vehiculo_viaje " +
                "WHERE v.fecha >= CURRENT_DATE AND u.fecha_baja IS NULL AND v.cancelado = 0";

        Connection conn = null;

        try {
            conn = ConnectionDB.getInstancia().getConn();
            try (PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    viajes.add(mapViajeFromJoin(rs, true));
                }
                logger.info("Obtenidos {} viajes activos", viajes.size());
            }
        } catch (SQLException e) {
            logger.error("Error al obtener todos los viajes", e);
            throw new DataAccessException("Error al obtener todos los viajes", e);
        } finally {
            ConnectionDB.getInstancia().releaseConn();
        }
        return viajes;
    }

    public LinkedList<Viaje> getAllBySearch(String origen, String destino, String fecha) {
        logger.debug("Buscando viajes - origen: {}, destino: {}, fecha: {}", origen, destino, fecha);
        LinkedList<Viaje> viajes = new LinkedList<>();

        if (origen == null || origen.trim().isEmpty() || destino == null || destino.trim().isEmpty()) {
            logger.warn("Parámetros de búsqueda incompletos");
            return viajes;
        }

        String query;
        java.sql.Date fechaSql = null;
        boolean tieneFecha = false;

        if (fecha != null && !fecha.trim().isEmpty()) {
            try {
                fechaSql = java.sql.Date.valueOf(fecha.trim());
                tieneFecha = true;
            } catch (IllegalArgumentException e) {
                logger.warn("Formato de fecha inválido: {}. Se ignorará el filtro de fecha.", fecha);
            }
        }

        if (tieneFecha && fechaSql != null) {
            query = "SELECT v.*, " +
                    "u.id_usuario as conductor_id, u.nombre as conductor_nombre, " +
                    "u.apellido as conductor_apellido, u.correo as conductor_correo, " +
                    "u.telefono as conductor_telefono, " +
                    "veh.id_vehiculo, veh.patente, veh.modelo, veh.anio " +
                    "FROM viajes v " +
                    "INNER JOIN usuarios u ON u.id_usuario = v.id_conductor " +
                    "INNER JOIN vehiculos veh ON veh.id_vehiculo = v.id_vehiculo_viaje " +
                    "WHERE v.origen COLLATE utf8mb4_general_ci LIKE ? " +
                    "AND v.destino COLLATE utf8mb4_general_ci LIKE ? " +
                    "AND v.fecha = ? " +
                    "AND v.cancelado = 0 " +
                    "AND u.fecha_baja IS NULL " +
                    "ORDER BY v.fecha ASC, v.lugares_disponibles DESC";
        } else {
            query = "SELECT v.*, " +
                    "u.id_usuario as conductor_id, u.nombre as conductor_nombre, " +
                    "u.apellido as conductor_apellido, u.correo as conductor_correo, " +
                    "u.telefono as conductor_telefono, " +
                    "veh.id_vehiculo, veh.patente, veh.modelo, veh.anio " +
                    "FROM viajes v " +
                    "INNER JOIN usuarios u ON u.id_usuario = v.id_conductor " +
                    "INNER JOIN vehiculos veh ON veh.id_vehiculo = v.id_vehiculo_viaje " +
                    "WHERE v.origen COLLATE utf8mb4_general_ci LIKE ? " +
                    "AND v.destino COLLATE utf8mb4_general_ci LIKE ? " +
                    "AND v.fecha >= CURRENT_DATE " +
                    "AND v.cancelado = 0 " +
                    "AND u.fecha_baja IS NULL " +
                    "ORDER BY v.fecha ASC, v.lugares_disponibles DESC";
        }

        Connection conn = null;

        try {
            conn = ConnectionDB.getInstancia().getConn();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, origen.trim());
                stmt.setString(2, destino.trim());

                if (tieneFecha && fechaSql != null) {
                    stmt.setDate(3, fechaSql);
                    try (ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            viajes.add(mapViajeFromJoin(rs, true));
                        }
                    }
                } else {
                    try (ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            viajes.add(mapViajeFromJoin(rs, true));
                        }
                    }
                }
                logger.info("Búsqueda completada - origen: {}, destino: {}, fecha: {}. Resultados: {}",
                        origen, destino, fecha, viajes.size());
            }
        } catch (SQLException e) {
            logger.error("Error al buscar viajes - origen: {}, destino: {}, fecha: {}", origen, destino, fecha, e);
            throw new DataAccessException("Error al buscar viajes", e);
        } finally {
            ConnectionDB.getInstancia().releaseConn();
        }

        return viajes;
    }

    public Viaje getByViaje(int id_viaje) {
        logger.debug("Buscando viaje con ID: {}", id_viaje);
        Viaje viaje = null;

        String query = "SELECT v.*, " +
                "u.id_usuario as conductor_id, u.nombre as conductor_nombre, " +
                "u.apellido as conductor_apellido, u.correo as conductor_correo, " +
                "u.telefono as conductor_telefono, " +
                "veh.id_vehiculo, veh.patente, veh.modelo, veh.anio " +
                "FROM viajes v " +
                "INNER JOIN usuarios u ON u.id_usuario = v.id_conductor " +
                "INNER JOIN vehiculos veh ON veh.id_vehiculo = v.id_vehiculo_viaje " +
                "WHERE v.id_viaje = ?";

        Connection conn = null;

        try {
            conn = ConnectionDB.getInstancia().getConn();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, id_viaje);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        viaje = mapViajeFromJoin(rs, false);
                        logger.debug("Viaje encontrado: ID {}", id_viaje);
                    } else {
                        logger.warn("Viaje no encontrado: ID {}", id_viaje);
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error al obtener viaje ID {}", id_viaje, e);
            throw new DataAccessException("Error al obtener viaje", e);
        } finally {
            ConnectionDB.getInstancia().releaseConn();
        }
        return viaje;
    }

    public LinkedList<Viaje> getByUser(Usuario u) {
        logger.debug("Obteniendo viajes del usuario ID: {}", u.getIdUsuario());
        LinkedList<Viaje> viajes = new LinkedList<>();

        String query = "SELECT v.*, " +
                "u_conductor.id_usuario as conductor_id, u_conductor.nombre as conductor_nombre, " +
                "u_conductor.apellido as conductor_apellido, u_conductor.correo as conductor_correo, " +
                "u_conductor.telefono as conductor_telefono, " +
                "veh.id_vehiculo, veh.patente, veh.modelo, veh.anio " +
                "FROM viajes v " +
                "INNER JOIN usuarios u_conductor ON u_conductor.id_usuario = v.id_conductor " +
                "INNER JOIN vehiculos veh ON veh.id_vehiculo = v.id_vehiculo_viaje " +
                "WHERE v.id_conductor = ?";

        Connection conn = null;

        try {
            conn = ConnectionDB.getInstancia().getConn();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, u.getIdUsuario());
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        viajes.add(mapViajeFromJoin(rs, false));
                    }
                }
            }
            logger.info("Encontrados {} viajes para usuario ID: {}", viajes.size(), u.getIdUsuario());
        } catch (SQLException e) {
            logger.error("Error al obtener viajes del usuario ID {}", u.getIdUsuario(), e);
            throw new DataAccessException("Error al obtener viajes del usuario", e);
        } finally {
            ConnectionDB.getInstancia().releaseConn();
        }
        return viajes;
    }

    public void updateCantidad(int idViaje, int cantPasajeros) {
        logger.info("Actualizando lugares disponibles para viaje ID: {} - Nueva cantidad: {}", idViaje, cantPasajeros);

        String sql = "UPDATE viajes SET lugares_disponibles = ? WHERE id_viaje = ?";
        Connection conn = null;

        try {
            conn = ConnectionDB.getInstancia().getConn();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, cantPasajeros);
                stmt.setInt(2, idViaje);

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    logger.info("Lugares actualizados exitosamente para viaje ID: {}", idViaje);
                } else {
                    logger.warn("No se encontró viaje con ID: {}", idViaje);
                }
            }
        } catch (SQLException e) {
            logger.error("Error al actualizar lugares del viaje ID {}", idViaje, e);
            throw new DataAccessException("Error al actualizar lugares del viaje", e);
        } finally {
            ConnectionDB.getInstancia().releaseConn();
        }
    }

    public boolean cancelarViaje(int id_viaje) {
        logger.info("Cancelando viaje ID: {}", id_viaje);

        String sql = "UPDATE viajes SET cancelado = true WHERE id_viaje = ?";
        Connection conn = null;

        try {
            conn = ConnectionDB.getInstancia().getConn();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id_viaje);

                int rowsAffected = stmt.executeUpdate();
                boolean cancelada = rowsAffected > 0;

                if (cancelada) {
                    logger.info("Viaje cancelado exitosamente: ID {}", id_viaje);
                } else {
                    logger.warn("No se pudo cancelar el viaje ID: {}", id_viaje);
                }
                return cancelada;
            }
        } catch (SQLException e) {
            logger.error("Error al cancelar viaje ID {}", id_viaje, e);
            throw new DataAccessException("Error al cancelar viaje", e);
        } finally {
            ConnectionDB.getInstancia().releaseConn();
        }
    }

    public void update(Viaje v, int id_viaje) {
        logger.info("Actualizando viaje ID: {}", id_viaje);

        String sql = "UPDATE viajes SET fecha=?, lugares_disponibles=?, origen=?, destino=?, " +
                "precio_unitario=?, cancelado=?, id_conductor=?, lugar_salida=?, id_vehiculo_viaje=? WHERE id_viaje=?";
        Connection conn = null;

        try {
            conn = ConnectionDB.getInstancia().getConn();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setDate(1, v.getFecha());
                stmt.setInt(2, v.getLugares_disponibles());
                stmt.setString(3, v.getOrigen());
                stmt.setString(4, v.getDestino());
                stmt.setDouble(5, v.getPrecio_unitario());
                stmt.setBoolean(6, v.isCancelado());
                stmt.setInt(7, v.getConductor().getIdUsuario());
                stmt.setString(8, v.getLugar_salida());
                stmt.setInt(9, v.getVehiculo().getId_vehiculo());
                stmt.setInt(10, id_viaje);

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    logger.info("Viaje actualizado exitosamente: ID {}", id_viaje);
                } else {
                    logger.warn("No se encontró viaje con ID: {}", id_viaje);
                }
            }
        } catch (SQLException e) {
            logger.error("Error al actualizar viaje ID {}", id_viaje, e);
            throw new DataAccessException("Error al actualizar viaje", e);
        } finally {
            ConnectionDB.getInstancia().releaseConn();
        }
    }

    public void add(Viaje v) {
        logger.info("Creando nuevo viaje para conductor ID: {}", v.getConductor().getIdUsuario());

        String sql = "INSERT INTO viajes(fecha, lugares_disponibles, origen, destino, precio_unitario, " +
                "cancelado, id_conductor, lugar_salida, id_vehiculo_viaje) VALUES (?,?,?,?,?,?,?,?,?)";
        Connection conn = null;

        try {
            conn = ConnectionDB.getInstancia().getConn();
            try (PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                stmt.setDate(1, v.getFecha());
                stmt.setInt(2, v.getLugares_disponibles());
                stmt.setString(3, v.getOrigen());
                stmt.setString(4, v.getDestino());
                stmt.setDouble(5, v.getPrecio_unitario());
                stmt.setBoolean(6, false);
                stmt.setInt(7, v.getConductor().getIdUsuario());
                stmt.setString(8, v.getLugar_salida());
                stmt.setInt(9, v.getVehiculo().getId_vehiculo());

                int affectedRows = stmt.executeUpdate();
                if (affectedRows > 0) {
                    try (ResultSet keys = stmt.getGeneratedKeys()) {
                        if (keys.next()) {
                            v.setIdViaje(keys.getInt(1));
                            logger.info("Viaje creado exitosamente: ID {}", v.getIdViaje());
                        }
                    }
                } else {
                    logger.error("No se pudo crear el viaje");
                    throw new DataAccessException("No se pudo crear el viaje");
                }
            }
        } catch (SQLException e) {
            logger.error("Error al crear nuevo viaje", e);
            throw new DataAccessException("Error al crear nuevo viaje", e);
        } finally {
            ConnectionDB.getInstancia().releaseConn();
        }
    }

    public void delete(Viaje v) {
        logger.info("Eliminando viaje ID: {}", v.getIdViaje());

        String sql = "DELETE FROM viajes WHERE id_viaje = ?";
        Connection conn = null;

        try {
            conn = ConnectionDB.getInstancia().getConn();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, v.getIdViaje());

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    logger.info("Viaje eliminado exitosamente: ID {}", v.getIdViaje());
                } else {
                    logger.warn("No se encontró viaje con ID: {}", v.getIdViaje());
                }
            }
        } catch (SQLException e) {
            logger.error("Error al eliminar viaje ID {}", v.getIdViaje(), e);
            throw new DataAccessException("Error al eliminar viaje", e);
        } finally {
            ConnectionDB.getInstancia().releaseConn();
        }
    }

    private Viaje mapViajeFromJoin(ResultSet rs, boolean puntuar) throws SQLException {

        Viaje v = new Viaje();
        v.setIdViaje(rs.getInt("id_viaje"));
        v.setFecha(rs.getDate("fecha"));
        v.setLugares_disponibles(rs.getInt("lugares_disponibles"));
        v.setOrigen(rs.getString("origen"));
        v.setDestino(rs.getString("destino"));
        v.setPrecio_unitario(rs.getDouble("precio_unitario"));
        v.setCancelado(rs.getBoolean("cancelado"));
        v.setLugar_salida(rs.getString("lugar_salida"));


        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setId_vehiculo(rs.getInt("id_vehiculo"));
        vehiculo.setPatente(rs.getString("patente"));
        vehiculo.setModelo(rs.getString("modelo"));
        vehiculo.setAnio(rs.getInt("anio"));
        v.setVehiculo(vehiculo);


        Usuario conductor = new Usuario();
        conductor.setIdUsuario(rs.getInt("conductor_id"));
        conductor.setNombre(rs.getString("conductor_nombre"));
        conductor.setApellido(rs.getString("conductor_apellido"));
        conductor.setCorreo(rs.getString("conductor_correo"));
        conductor.setTelefono(rs.getString("conductor_telefono"));

        if (puntuar) {
            Usuario conductorPuntuado = calcularPromedioPuntuacion(conductor);
            v.setConductor(conductorPuntuado);
        } else {
            v.setConductor(conductor);
        }

        return v;
    }

    private Usuario calcularPromedioPuntuacion(Usuario conductor) {
        try {
            Map<String, Object> puntuacion_cantidad = fDAO.getUserRating(conductor);
            double promedio = (double) puntuacion_cantidad.get("promedio");
            int cantidad = (int) puntuacion_cantidad.get("cantidad");

            conductor.setPromedio_puntuacion(promedio);
            conductor.setCantidad_que_puntuaron(cantidad);
            return conductor;
        } catch (Exception e) {
            logger.error("Error al calcular puntuación para conductor ID {}", conductor.getIdUsuario(), e);
            return conductor;
        }
    }
}
