package data;

import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import entidades.*;
import data.exceptions.DataAccessException;

public class ReservaDAO {
    private static final Logger logger = LoggerFactory.getLogger(ReservaDAO.class);
    private String universalQuery = "SELECT " +
            "r.id_reserva, r.fecha_reserva, r.estado, r.cantidad_pasajeros_reservada, " +
            "r.reserva_cancelada, r.codigo_reserva, r.feedback_token, " +
            "v.id_viaje, v.fecha, v.lugares_disponibles, v.origen, v.destino, " +
            "v.precio_unitario, v.cancelado, v.lugar_salida, v.id_vehiculo_viaje, " +
            "u_conductor.id_usuario as conductor_id, u_conductor.nombre as conductor_nombre, " +
            "u_conductor.apellido as conductor_apellido, u_conductor.correo as conductor_correo, " +
            "u_conductor.telefono as conductor_telefono, " +
            "u_pasajero.id_usuario as pasajero_id, u_pasajero.nombre as pasajero_nombre, " +
            "u_pasajero.apellido as pasajero_apellido, u_pasajero.correo as pasajero_correo, " +
            "u_pasajero.telefono as pasajero_telefono, " +
            "veh.id_vehiculo, veh.patente, veh.modelo, veh.anio " +
            "FROM reservas r " +
            "INNER JOIN viajes v ON r.id_viaje = v.id_viaje " +
            "INNER JOIN usuarios u_conductor ON u_conductor.id_usuario = v.id_conductor " +
            "INNER JOIN usuarios u_pasajero ON u_pasajero.id_usuario = r.id_pasajero_reserva " +
            "INNER JOIN vehiculos veh ON veh.id_vehiculo = v.id_vehiculo_viaje ";

    public ReservaDAO() {
    }

    public LinkedList<Reserva> getAll() {
        logger.debug("Obteniendo todas las reservas");
        LinkedList<Reserva> reservas = new LinkedList<>();

        String query = universalQuery +
                "WHERE r.activo = TRUE";

        try (
                Connection conn = ConnectionDB.getInstancia().getConn();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)
        ) {
            while (rs.next()) {
                reservas.add(mapFullReservaFromJoin(rs));
            }
            logger.info("Obtenidas {} reservas", reservas.size());
            return reservas;

        } catch (SQLException e) {
            logger.error("Error al obtener todas las reservas - Estado: {} - Código: {}",
                    e.getSQLState(), e.getErrorCode(), e);
            throw new DataAccessException("Error al obtener todas las reservas", e);
        }
    }

    public Reserva getByReserva(int id_reserva) {
        logger.debug("Buscando reserva con ID: {}", id_reserva);

        String query = universalQuery +
                "WHERE r.id_reserva = ? AND r.activo = TRUE";

        try (
                Connection conn = ConnectionDB.getInstancia().getConn();
                PreparedStatement stmt = conn.prepareStatement(query)
        ) {
            stmt.setInt(1, id_reserva);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    logger.debug("Reserva encontrada: ID {}", id_reserva);
                    return mapFullReservaFromJoin(rs);
                } else {
                    logger.warn("Reserva no encontrada: ID {}", id_reserva);
                    return null;
                }
            }

        } catch (SQLException e) {
            logger.error("Error al obtener reserva ID {} - Estado: {} - Código: {}",
                    id_reserva, e.getSQLState(), e.getErrorCode(), e);
            throw new DataAccessException("Error al obtener reserva", e);
        }
    }

    public Reserva getByToken(String token) {
        Reserva reserva = null;
        String query = universalQuery +
                "WHERE r.feedback_token = ?";

        try (Connection conn = ConnectionDB.getInstancia().getConn();
             PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setString(1, token);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        reserva = mapFullReservaFromJoin(rs);
                    }
                }

        } catch (SQLException e) {
            logger.error("Error al obtener Reserva con token: {}", token, e);
            throw new DataAccessException("Error al obtener la reserva por token", e);
        }
        return reserva;
    }

    public int obtenerCantidad(int idReserva) {
        logger.debug("Obteniendo cantidad de pasajeros para reserva ID: {}", idReserva);

        String query = "SELECT cantidad_pasajeros_reservada FROM reservas WHERE id_reserva = ? AND activo = TRUE";

        try (
                Connection conn = ConnectionDB.getInstancia().getConn();
                PreparedStatement stmt = conn.prepareStatement(query)
        ) {
            stmt.setInt(1, idReserva);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int cantidad = rs.getInt("cantidad_pasajeros_reservada");
                    logger.debug("Cantidad obtenida: {} para reserva ID: {}", cantidad, idReserva);
                    return cantidad;
                } else {
                    logger.warn("Reserva no encontrada: ID {}", idReserva);
                    throw new DataAccessException("Reserva no encontrada con ID: " + idReserva);
                }
            }

        } catch (SQLException e) {
            logger.error("Error al obtener cantidad para reserva ID {} - Estado: {} - Código: {}",
                    idReserva, e.getSQLState(), e.getErrorCode(), e);
            throw new DataAccessException("Error al obtener cantidad para reserva", e);
        }
    }

    public LinkedList<Reserva> getByUser(Usuario usuario) {
        logger.debug("Obteniendo reservas para usuario ID: {}", usuario.getIdUsuario());

        validateUsuario(usuario);
        LinkedList<Reserva> reservas = new LinkedList<>();

        String query = universalQuery +
                "WHERE r.id_pasajero_reserva = ? AND r.activo = TRUE";

        try (
                Connection conn = ConnectionDB.getInstancia().getConn();
                PreparedStatement stmt = conn.prepareStatement(query)
        ) {
            stmt.setInt(1, usuario.getIdUsuario());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reservas.add(mapFullReservaFromJoin(rs));
                }
            }

            logger.info("Encontradas {} reservas para usuario ID: {}", reservas.size(), usuario.getIdUsuario());
            return reservas;

        } catch (SQLException e) {
            logger.error("Error al obtener reservas por usuario ID {} - Estado: {} - Código: {}",
                    usuario.getIdUsuario(), e.getSQLState(), e.getErrorCode(), e);
            throw new DataAccessException("Error al obtener reservas por usuario", e);
        }
    }

    public LinkedList<Reserva> getReservasByViaje(int idViaje, boolean all) {
        logger.debug("Obteniendo reservas para viaje ID: {}", idViaje);

        LinkedList<Reserva> reservas = new LinkedList<>();
        String query = universalQuery +
                "WHERE r.id_viaje = ?";

        if (!all) {
            query += " AND r.reserva_cancelada = false";
        }

        try (
                Connection conn = ConnectionDB.getInstancia().getConn();
                PreparedStatement stmt = conn.prepareStatement(query)
        ) {
            stmt.setInt(1, idViaje);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reservas.add(mapFullReservaFromJoin(rs));
                }
            }

            logger.info("Encontradas {} reservas para viaje ID: {}", reservas.size(), idViaje);
            return reservas;

        } catch (SQLException e) {
            logger.error("Error al obtener reservas por viaje ID {} - Estado: {} - Código: {}",
                    idViaje, e.getSQLState(), e.getErrorCode(), e);
            throw new DataAccessException("Error al obtener reservas por viaje", e);
        }
    }

    public LinkedList<Reserva> getReservasForFeedback() {
        logger.debug("Obteniendo reservas para feedback");

        LinkedList<Reserva> reservas = new LinkedList<>();

        String query = universalQuery +
                "LEFT JOIN feedback f ON f.id_reserva = r.id_reserva " +
                "WHERE v.fecha = ? " +
                "AND r.estado = 'CONFIRMADA' " +
                "AND r.reserva_cancelada = false " +
                "AND f.id_reserva IS NULL";

        try (
                Connection conn = ConnectionDB.getInstancia().getConn();
                PreparedStatement stmt = conn.prepareStatement(query)
        ) {
            LocalDate ayer = LocalDate.now().minusDays(1);
            stmt.setDate(1, java.sql.Date.valueOf(ayer));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reservas.add(mapFullReservaFromJoin(rs));
                }
            }

            logger.info("Obtenidas {} reservas para feedback", reservas.size());
            return reservas;

        } catch (SQLException e) {
            logger.error("Error al obtener reservas para feedback - Estado: {} - Código: {}",
                    e.getSQLState(), e.getErrorCode(), e);
            throw new DataAccessException("Error al obtener reservas para feedback", e);
        }
    }

    public LinkedList<Reserva> obtenerReservasRecientes(int limite) {
        logger.debug("Obteniendo {} reservas más recientes", limite);

        String query = universalQuery +
                "WHERE r.activo = TRUE " +
                "ORDER BY r.id_reserva DESC LIMIT ?";

        LinkedList<Reserva> reservas = new LinkedList<>();

        try (
                Connection conn = ConnectionDB.getInstancia().getConn();
                PreparedStatement stmt = conn.prepareStatement(query)
        ) {
            stmt.setInt(1, limite);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reservas.add(mapFullReservaFromJoin(rs));
                }
            }

            logger.info("Obtenidas {} reservas recientes", reservas.size());
            return reservas;

        } catch (SQLException e) {
            logger.error("Error al obtener reservas recientes - Estado: {} - Código: {}",
                    e.getSQLState(), e.getErrorCode(), e);
            throw new DataAccessException("Error al obtener reservas recientes", e);
        }
    }

    public void add(Reserva reserva) {
        logger.info("Creando nueva reserva para usuario ID: {}", reserva.getPasajero().getIdUsuario());

        String query = "INSERT INTO reservas(fecha_reserva, cantidad_pasajeros_reservada, " +
                "reserva_cancelada, id_viaje, id_pasajero_reserva, codigo_reserva, estado) " +
                "VALUES(?,?,?,?,?,?,?)";

        try (
                Connection conn = ConnectionDB.getInstancia().getConn();
                PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)
        ) {
            stmt.setString(1, reserva.getFecha_reserva());
            stmt.setInt(2, reserva.getCantidad_pasajeros_reservada());
            stmt.setBoolean(3, reserva.isReserva_cancelada());
            stmt.setInt(4, reserva.getViaje().getIdViaje());
            stmt.setInt(5, reserva.getPasajero().getIdUsuario());
            stmt.setInt(6, reserva.getCodigo_reserva());
            stmt.setString(7, reserva.getEstado());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        reserva.setIdReserva(keys.getInt(1));
                        logger.info("Reserva creada exitosamente: ID {}", reserva.getIdReserva());
                    }
                }
            } else {
                logger.error("No se pudo crear la reserva");
                throw new DataAccessException("No se pudo crear la reserva");
            }

        } catch (SQLException e) {
            logger.error("Error al crear reserva - Estado: {} - Código: {}",
                    e.getSQLState(), e.getErrorCode(), e);
            throw new DataAccessException("Error al agregar reserva", e);
        }
    }

    public void actualizarEstado(int idReserva, String nuevoEstado) {
        logger.info("Actualizando estado de reserva ID: {} a {}", idReserva, nuevoEstado);

        String query = "UPDATE reservas SET estado = ? WHERE id_reserva = ? AND activo = TRUE";

        try (
                Connection conn = ConnectionDB.getInstancia().getConn();
                PreparedStatement stmt = conn.prepareStatement(query)
        ) {
            stmt.setString(1, nuevoEstado);
            stmt.setInt(2, idReserva);

            int affected = stmt.executeUpdate();
            checkAffectedRows(affected, "actualizar estado");
            logger.info("Estado de reserva ID {} actualizado a {}", idReserva, nuevoEstado);

        } catch (SQLException e) {
            logger.error("Error al actualizar estado de reserva ID {} - Estado: {} - Código: {}",
                    idReserva, e.getSQLState(), e.getErrorCode(), e);
            throw new DataAccessException("Error al actualizar estado de reserva", e);
        }
    }

    public void update(Reserva reserva, int idReserva) {
        logger.info("Actualizando reserva ID: {}", idReserva);

        String query = "UPDATE reservas SET cantidad_pasajeros_reservada = ?, reserva_cancelada = ?, " +
                "id_viaje = ?, id_pasajero_reserva = ? WHERE id_reserva = ? AND activo = TRUE";

        try (
                Connection conn = ConnectionDB.getInstancia().getConn();
                PreparedStatement stmt = conn.prepareStatement(query)
        ) {
            stmt.setInt(1, reserva.getCantidad_pasajeros_reservada());
            stmt.setBoolean(2, reserva.isReserva_cancelada());
            stmt.setInt(3, reserva.getViaje().getIdViaje());
            stmt.setInt(4, reserva.getPasajero().getIdUsuario());
            stmt.setInt(5, idReserva);

            int affected = stmt.executeUpdate();
            checkAffectedRows(affected, "actualizar");
            logger.info("Reserva actualizada exitosamente: ID {}", idReserva);

        } catch (SQLException e) {
            logger.error("Error al actualizar reserva ID {} - Estado: {} - Código: {}",
                    idReserva, e.getSQLState(), e.getErrorCode(), e);
            throw new DataAccessException("Error al actualizar reserva", e);
        }
    }

    public void guardarToken(int idReserva, String token) {
        logger.info("Guardando token para reserva ID: {}", idReserva);

        String query = "UPDATE reservas SET feedback_token = ? WHERE id_reserva = ? AND activo = TRUE";

        try (
                Connection conn = ConnectionDB.getInstancia().getConn();
                PreparedStatement stmt = conn.prepareStatement(query)
        ) {
            stmt.setString(1, token);
            stmt.setInt(2, idReserva);

            int affected = stmt.executeUpdate();
            checkAffectedRows(affected, "guardar token");
            logger.info("Token guardado para reserva ID: {}", idReserva);

        } catch (SQLException e) {
            logger.error("Error al guardar token para reserva ID {} - Estado: {} - Código: {}",
                    idReserva, e.getSQLState(), e.getErrorCode(), e);
            throw new DataAccessException("Error al guardar token en la reserva", e);
        }
    }

    public boolean cancelarReserva(int idReserva) {
        logger.info("Cancelando reserva ID: {}", idReserva);

        String query = "UPDATE reservas SET reserva_cancelada = true, estado = 'CANCELADA' WHERE id_reserva = ? AND activo = TRUE";

        try (
                Connection conn = ConnectionDB.getInstancia().getConn();
                PreparedStatement stmt = conn.prepareStatement(query)
        ) {
            stmt.setInt(1, idReserva);

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                logger.info("Reserva cancelada exitosamente: ID {}", idReserva);
                return true;
            } else {
                logger.warn("No se encontró reserva con ID: {}", idReserva);
                return false;
            }

        } catch (SQLException e) {
            logger.error("Error al cancelar reserva ID {} - Estado: {} - Código: {}",
                    idReserva, e.getSQLState(), e.getErrorCode(), e);
            throw new DataAccessException("Error al cancelar reserva", e);
        }
    }

    public boolean delete(int idReserva) {
        logger.info("Eliminando reserva ID: {}", idReserva);

        String query = "UPDATE reservas SET activo = FALSE WHERE id_reserva = ?";

        try (
                Connection conn = ConnectionDB.getInstancia().getConn();
                PreparedStatement stmt = conn.prepareStatement(query)
        ) {
            stmt.setInt(1, idReserva);

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                logger.info("Reserva eliminada exitosamente: ID {}", idReserva);
                return true;
            } else {
                logger.warn("No se encontró reserva a eliminar con ID: {}", idReserva);
                return false;
            }

        } catch (SQLException e) {
            logger.error("Error al eliminar reserva ID {} - Estado: {} - Código: {}",
                    idReserva, e.getSQLState(), e.getErrorCode(), e);
            throw new DataAccessException("Error al eliminar reserva", e);
        }
    }
    
    public double calcularIngresosTotales() {
        logger.debug("Calculando ingresos totales");
        
        String query = "SELECT SUM(v.precio_unitario * r.cantidad_pasajeros_reservada) as ingresos_totales " +
                       "FROM reservas r " +
                       "INNER JOIN viajes v ON r.id_viaje = v.id_viaje " +
                       "WHERE r.estado = 'CONFIRMADA' " +
                       "AND r.reserva_cancelada = false " +
                       "AND r.activo = true " +
                       "AND v.activo = true";

        try (
            Connection conn = ConnectionDB.getInstancia().getConn();
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery()
        ) {
            if (rs.next()) {
                double ingresos = rs.getDouble("ingresos_totales");
                logger.info("Ingresos totales calculados: {}", ingresos);
                return ingresos;
            } else {
                logger.info("No se encontraron reservas confirmadas para calcular ingresos totales");
                return 0.0;
            }

        } catch (SQLException e) {
            logger.error("Error al calcular ingresos totales - Estado: {} - Código: {}",
                    e.getSQLState(), e.getErrorCode(), e);
            throw new DataAccessException("Error al calcular ingresos totales", e);
        }
    }

    public double calcularIngresosMesActual() {
        logger.debug("Calculando ingresos del mes actual");
        
        String query = "SELECT SUM(v.precio_unitario * r.cantidad_pasajeros_reservada) as ingresos_mes " +
                       "FROM reservas r " +
                       "INNER JOIN viajes v ON r.id_viaje = v.id_viaje " +
                       "WHERE r.estado = 'CONFIRMADA' " +
                       "AND r.reserva_cancelada = false " +
                       "AND r.activo = true " +
                       "AND v.cancelado = false " +
                       "AND MONTH(r.fecha_reserva) = MONTH(CURRENT_DATE()) " +
                       "AND YEAR(r.fecha_reserva) = YEAR(CURRENT_DATE())";

        try (
            Connection conn = ConnectionDB.getInstancia().getConn();
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery()
        ) {
            if (rs.next()) {
                double ingresos = rs.getDouble("ingresos_mes");
                logger.info("Ingresos del mes actual calculados: {}", ingresos);
                return ingresos;
            } else {
                logger.info("No se encontraron reservas confirmadas para el mes actual");
                return 0.0;
            }

        } catch (SQLException e) {
            logger.error("Error al calcular ingresos del mes actual - Estado: {} - Código: {}",
                    e.getSQLState(), e.getErrorCode(), e);
            throw new DataAccessException("Error al calcular ingresos del mes actual", e);
        }
    }

    public double calcularPromedioPorReserva() {
        logger.debug("Calculando promedio por reserva");
        
        String query = "SELECT AVG(v.precio_unitario * r.cantidad_pasajeros_reservada) as promedio_reserva " +
                       "FROM reservas r " +
                       "INNER JOIN viajes v ON r.id_viaje = v.id_viaje " +
                       "WHERE r.estado = 'CONFIRMADA' " +
                       "AND r.reserva_cancelada = false " +
                       "AND r.activo = true " +
                       "AND v.cancelado = false";

        try (
            Connection conn = ConnectionDB.getInstancia().getConn();
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery()
        ) {
            if (rs.next()) {
                double promedio = rs.getDouble("promedio_reserva");
                logger.info("Promedio por reserva calculado: {}", promedio);
                return promedio;
            } else {
                logger.info("No se pudo calcular el promedio por reserva");
                return 0.0;
            }

        } catch (SQLException e) {
            logger.error("Error al calcular promedio por reserva - Estado: {} - Código: {}",
                    e.getSQLState(), e.getErrorCode(), e);
            throw new DataAccessException("Error al calcular promedio por reserva", e);
        }
    }

    public Map<String, Object> obtenerEstadisticasReservas() {
        logger.debug("Obteniendo estadísticas generales de reservas");
        
        Map<String, Object> estadisticas = new HashMap<>();
        
        String query = "SELECT " +
                       "COUNT(*) as total_reservas, " +
                       "SUM(CASE WHEN estado = 'CONFIRMADA' AND reserva_cancelada = false THEN 1 ELSE 0 END) as reservas_confirmadas, " +
                       "SUM(CASE WHEN reserva_cancelada = true THEN 1 ELSE 0 END) as reservas_canceladas, " +
                       "SUM(CASE WHEN estado = 'CONFIRMADA' AND reserva_cancelada = false THEN cantidad_pasajeros_reservada ELSE 0 END) as total_pasajeros " +
                       "FROM reservas " +
                       "WHERE activo = true";

        try (
            Connection conn = ConnectionDB.getInstancia().getConn();
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery()
        ) {
            if (rs.next()) {
                estadisticas.put("totalReservas", rs.getInt("total_reservas"));
                estadisticas.put("reservasConfirmadas", rs.getInt("reservas_confirmadas"));
                estadisticas.put("reservasCanceladas", rs.getInt("reservas_canceladas"));
                estadisticas.put("totalPasajeros", rs.getInt("total_pasajeros"));
                
                int totalReservas = rs.getInt("total_reservas");
                
                logger.info("Estadísticas de reservas obtenidas: total={}, confirmadas={}, canceladas={}",
                        totalReservas, rs.getInt("reservas_confirmadas"));
            }

        } catch (SQLException e) {
            logger.error("Error al obtener estadísticas de reservas - Estado: {} - Código: {}",
                    e.getSQLState(), e.getErrorCode(), e);
            throw new DataAccessException("Error al obtener estadísticas de reservas", e);
        }
        
        return estadisticas;
    }


    private Reserva mapFullReservaFromJoin(ResultSet rs) throws SQLException {
        Reserva reserva = new Reserva();

        reserva.setIdReserva(rs.getInt("id_reserva"));
        reserva.setFecha_reserva(rs.getString("fecha_reserva"));
        reserva.setCantidad_pasajeros_reservada(rs.getInt("cantidad_pasajeros_reservada"));
        reserva.setReserva_cancelada(rs.getBoolean("reserva_cancelada"));
        reserva.setEstado(rs.getString("estado"));
        reserva.setCodigo_reserva(rs.getInt("codigo_reserva"));
        reserva.setFeedback_token(rs.getString("feedback_token"));

        Viaje viaje = new Viaje();
        viaje.setIdViaje(rs.getInt("id_viaje"));
        viaje.setFecha(rs.getDate("fecha"));
        viaje.setLugares_disponibles(rs.getInt("lugares_disponibles"));
        viaje.setOrigen(rs.getString("origen"));
        viaje.setDestino(rs.getString("destino"));
        viaje.setPrecio_unitario(rs.getDouble("precio_unitario"));
        viaje.setCancelado(rs.getBoolean("cancelado"));
        viaje.setLugar_salida(rs.getString("lugar_salida"));

        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setId_vehiculo(rs.getInt("id_vehiculo"));
        vehiculo.setPatente(rs.getString("patente"));
        vehiculo.setModelo(rs.getString("modelo"));
        vehiculo.setAnio(rs.getInt("anio"));
        viaje.setVehiculo(vehiculo);

        Usuario conductor = new Usuario();
        conductor.setIdUsuario(rs.getInt("conductor_id"));
        conductor.setNombre(rs.getString("conductor_nombre"));
        conductor.setApellido(rs.getString("conductor_apellido"));
        conductor.setCorreo(rs.getString("conductor_correo"));
        conductor.setTelefono(rs.getString("conductor_telefono"));
        viaje.setConductor(conductor);

        Usuario pasajero = new Usuario();
        pasajero.setIdUsuario(rs.getInt("pasajero_id"));
        pasajero.setNombre(rs.getString("pasajero_nombre"));
        pasajero.setApellido(rs.getString("pasajero_apellido"));
        pasajero.setCorreo(rs.getString("pasajero_correo"));
        pasajero.setTelefono(rs.getString("pasajero_telefono"));

        reserva.setViaje(viaje);
        reserva.setPasajero(pasajero);

        return reserva;
    }


    private void validateUsuario(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario inválido");
        }
    }

    private void checkAffectedRows(int affected, String operation) {
        if (affected == 0) {
            throw new DataAccessException("Ningún registro afectado al " + operation);
        }
    }
}