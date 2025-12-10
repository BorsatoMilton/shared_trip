package data;

import data.exceptions.DataAccessException;
import entities.Usuario;
import entities.Vehiculo;
import entities.Viaje;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.Map;

public class ViajeDAO {
    private final FeedbackDAO fDAO = new FeedbackDAO();
    private static final Logger logger = LoggerFactory.getLogger(ViajeDAO.class);
    private static final String BASE_QUERY = "SELECT v.id_viaje, v.fecha, v.lugares_disponibles, " +
            "v.origen, v.destino, v.precio_unitario, v.cancelado, v.lugar_salida, " +
            "u.id_usuario as conductor_id, u.nombre as conductor_nombre, " +
            "u.apellido as conductor_apellido, u.correo as conductor_correo, " +
            "u.telefono as conductor_telefono, " +
            "veh.id_vehiculo, veh.patente, veh.modelo, veh.anio " +
            "FROM viajes v " +
            "INNER JOIN vehiculos veh ON veh.id_vehiculo = v.id_vehiculo_viaje " +
            "INNER JOIN usuarios u ON u.id_usuario = veh.usuario_duenio_id ";

    public LinkedList<Viaje> getAll(boolean all) {
        logger.debug("Obteniendo todos los viajes - todos: {}", all);
        LinkedList<Viaje> viajes = new LinkedList<>();

        String query = BASE_QUERY + "WHERE u.fecha_baja IS NULL AND v.activo = TRUE";

        if (!all) {
            query += " AND v.fecha >= CURRENT_DATE AND v.cancelado = 0";
        }
        query += " ORDER BY v.fecha DESC";

        try (Connection conn = ConnectionDB.getInstancia().getConn();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                viajes.add(mapViajeFromJoin(rs, true));
            }
            logger.info("Obtenidos {} viajes", viajes.size());

        } catch (SQLException e) {
            logger.error("Error al obtener todos los viajes - Estado: {} - Código: {}",
                    e.getSQLState(), e.getErrorCode(), e);
            throw new DataAccessException("Error al obtener todos los viajes", e);
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

        String query = BASE_QUERY +
                "WHERE v.origen COLLATE utf8mb4_general_ci LIKE ? " +
                "AND v.destino COLLATE utf8mb4_general_ci LIKE ? " +
                "AND v.cancelado = 0 " +
                "AND u.fecha_baja IS NULL " +
                "AND v.activo = TRUE ";

        if (tieneFecha && fechaSql != null) {
            query += "AND v.fecha = ? ";
        } else {
            query += "AND v.fecha >= CURRENT_DATE ";
        }

        query += "ORDER BY v.fecha DESC, v.lugares_disponibles DESC";

        try (Connection conn = ConnectionDB.getInstancia().getConn();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, "%" + origen.trim() + "%");
            stmt.setString(2, "%" + destino.trim() + "%");

            if (tieneFecha && fechaSql != null) {
                stmt.setDate(3, fechaSql);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    viajes.add(mapViajeFromJoin(rs, true));
                }
            }

            logger.info("Búsqueda completada. Resultados: {}", viajes.size());

        } catch (SQLException e) {
            logger.error("Error al buscar viajes - origen: {}, destino: {}, fecha: {} - Estado: {} - Código: {}",
                    origen, destino, fecha, e.getSQLState(), e.getErrorCode(), e);
            throw new DataAccessException("Error al buscar viajes", e);
        }

        return viajes;
    }

    public Viaje getByViaje(int id_viaje) {
        logger.debug("Buscando viaje con ID: {}", id_viaje);

        String query = BASE_QUERY + "WHERE v.id_viaje = ? AND v.activo = TRUE";

        try (Connection conn = ConnectionDB.getInstancia().getConn();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id_viaje);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    logger.debug("Viaje encontrado: ID {}", id_viaje);
                    return mapViajeFromJoin(rs, false);
                } else {
                    logger.warn("Viaje no encontrado: ID {}", id_viaje);
                    return null;
                }
            }

        } catch (SQLException e) {
            logger.error("Error al obtener viaje ID {} - Estado: {} - Código: {}",
                    id_viaje, e.getSQLState(), e.getErrorCode(), e);
            throw new DataAccessException("Error al obtener viaje", e);
        }
    }

    public LinkedList<Viaje> getByUser(Usuario u) {
        logger.debug("Obteniendo viajes del usuario ID: {}", u.getIdUsuario());
        LinkedList<Viaje> viajes = new LinkedList<>();

        String query = BASE_QUERY +
                "WHERE veh.usuario_duenio_id = ? AND v.activo = TRUE " +
                "ORDER BY v.fecha DESC";

        try (Connection conn = ConnectionDB.getInstancia().getConn();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, u.getIdUsuario());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    viajes.add(mapViajeFromJoin(rs, false));
                }
            }

            logger.info("Encontrados {} viajes para usuario ID: {}", viajes.size(), u.getIdUsuario());

        } catch (SQLException e) {
            logger.error("Error al obtener viajes del usuario ID {} - Estado: {} - Código: {}",
                    u.getIdUsuario(), e.getSQLState(), e.getErrorCode(), e);
            throw new DataAccessException("Error al obtener viajes del usuario", e);
        }

        return viajes;
    }

    public void updateCantidad(int idViaje, int cantPasajeros) {
        logger.info("Actualizando lugares disponibles para viaje ID: {} - Nueva cantidad: {}", idViaje, cantPasajeros);

        String sql = "UPDATE viajes SET lugares_disponibles = ? WHERE id_viaje = ? AND activo = TRUE";

        try (Connection conn = ConnectionDB.getInstancia().getConn();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, cantPasajeros);
            stmt.setInt(2, idViaje);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                logger.warn("No se encontró viaje con ID: {}", idViaje);
                throw new DataAccessException("Viaje no encontrado con ID: " + idViaje);
            }
            logger.info("Lugares actualizados exitosamente para viaje ID: {}", idViaje);

        } catch (SQLException e) {
            logger.error("Error al actualizar lugares del viaje ID {} - Estado: {} - Código: {}",
                    idViaje, e.getSQLState(), e.getErrorCode(), e);
            throw new DataAccessException("Error al actualizar lugares del viaje", e);
        }
    }

    public boolean cancelarViaje(int id_viaje) {
        logger.info("Cancelando viaje ID: {}", id_viaje);

        String sql = "UPDATE viajes SET cancelado = TRUE WHERE id_viaje = ? AND activo = TRUE";

        try (Connection conn = ConnectionDB.getInstancia().getConn();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id_viaje);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                logger.info("Viaje cancelado exitosamente: ID {}", id_viaje);
                return true;
            } else {
                logger.warn("No se pudo cancelar el viaje ID: {}", id_viaje);
                return false;
            }

        } catch (SQLException e) {
            logger.error("Error al cancelar viaje ID {} - Estado: {} - Código: {}",
                    id_viaje, e.getSQLState(), e.getErrorCode(), e);
            throw new DataAccessException("Error al cancelar viaje", e);
        }
    }

    public void update(Viaje v, int id_viaje) {
        logger.info("Actualizando viaje ID: {}", id_viaje);

        String sql = "UPDATE viajes SET fecha=?, lugares_disponibles=?, origen=?, destino=?, " +
                "precio_unitario=?, cancelado=?, lugar_salida=?, id_vehiculo_viaje=? " +
                "WHERE id_viaje=? AND activo = TRUE";

        try (Connection conn = ConnectionDB.getInstancia().getConn();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, v.getFecha());
            stmt.setInt(2, v.getLugares_disponibles());
            stmt.setString(3, v.getOrigen());
            stmt.setString(4, v.getDestino());
            stmt.setDouble(5, v.getPrecio_unitario());
            stmt.setBoolean(6, v.isCancelado());
            stmt.setString(7, v.getLugar_salida());
            stmt.setInt(8, v.getVehiculo().getId_vehiculo());
            stmt.setInt(9, id_viaje);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                logger.warn("No se encontró viaje con ID: {}", id_viaje);
                throw new DataAccessException("Viaje no encontrado con ID: " + id_viaje);
            }
            logger.info("Viaje actualizado exitosamente: ID {}", id_viaje);

        } catch (SQLException e) {
            logger.error("Error al actualizar viaje ID {} - Estado: {} - Código: {}",
                    id_viaje, e.getSQLState(), e.getErrorCode(), e);
            throw new DataAccessException("Error al actualizar viaje", e);
        }
    }

    public void add(Viaje v) {
        logger.info("Creando nuevo viaje para conductor ID: {}", v.getConductor().getIdUsuario());

        String sql = "INSERT INTO viajes(fecha, lugares_disponibles, origen, destino, precio_unitario, " +
                "cancelado, lugar_salida, id_vehiculo_viaje) VALUES (?,?,?,?,?,?,?,?)";

        try (Connection conn = ConnectionDB.getInstancia().getConn();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            stmt.setDate(1, v.getFecha());
            stmt.setInt(2, v.getLugares_disponibles());
            stmt.setString(3, v.getOrigen());
            stmt.setString(4, v.getDestino());
            stmt.setDouble(5, v.getPrecio_unitario());
            stmt.setBoolean(6, false);
            stmt.setString(7, v.getLugar_salida());
            stmt.setInt(8, v.getVehiculo().getId_vehiculo());

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

        } catch (SQLException e) {
            logger.error("Error al crear nuevo viaje - Estado: {} - Código: {}",
                    e.getSQLState(), e.getErrorCode(), e);
            throw new DataAccessException("Error al crear nuevo viaje", e);
        }
    }

    public void delete(Viaje v) {
        logger.info("Eliminando viaje ID: {}", v.getIdViaje());

        String sql = "UPDATE viajes SET activo = FALSE WHERE id_viaje = ?";

        try (Connection conn = ConnectionDB.getInstancia().getConn();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, v.getIdViaje());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                logger.warn("No se encontró viaje con ID: {}", v.getIdViaje());
                throw new DataAccessException("Viaje no encontrado con ID: " + v.getIdViaje());
            }
            logger.info("Viaje eliminado exitosamente: ID {}", v.getIdViaje());

        } catch (SQLException e) {
            logger.error("Error al eliminar viaje ID {} - Estado: {} - Código: {}",
                    v.getIdViaje(), e.getSQLState(), e.getErrorCode(), e);
            throw new DataAccessException("Error al eliminar viaje", e);
        }
    }

    public LinkedList<Viaje> obtenerViajesProximos(int limite) {
        logger.debug("Obteniendo {} viajes próximos", limite);

        String query = BASE_QUERY +
                "WHERE v.fecha >= CURRENT_DATE " +
                "AND v.cancelado = 0 " +
                "AND u.fecha_baja IS NULL " +
                "AND v.activo = TRUE " +
                "ORDER BY v.fecha ASC LIMIT ?";

        LinkedList<Viaje> viajes = new LinkedList<>();

        try (Connection conn = ConnectionDB.getInstancia().getConn();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, limite);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    viajes.add(mapViajeFromJoin(rs, true));
                }
            }

            logger.info("Obtenidos {} viajes próximos", viajes.size());
            return viajes;

        } catch (SQLException e) {
            logger.error("Error al obtener viajes próximos - Estado: {} - Código: {}",
                    e.getSQLState(), e.getErrorCode(), e);
            throw new DataAccessException("Error al obtener viajes próximos", e);
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
            conductor = calcularPromedioPuntuacion(conductor);
        }

        v.setConductor(conductor);
        return v;
    }

    private Usuario calcularPromedioPuntuacion(Usuario conductor) {
        try {
            Map<String, Object> puntuacion = fDAO.getUserRating(conductor);
            if (puntuacion != null) {
                double promedio = (double) puntuacion.get("promedio");
                int cantidad = (int) puntuacion.get("cantidad");
                conductor.setPromedio_puntuacion(promedio);
                conductor.setCantidad_que_puntuaron(cantidad);
            }
            return conductor;
        } catch (Exception e) {
            logger.warn("Error al calcular puntuación para conductor ID {} - continuando sin calificar",
                    conductor.getIdUsuario(), e);
            return conductor;
        }
    }
}