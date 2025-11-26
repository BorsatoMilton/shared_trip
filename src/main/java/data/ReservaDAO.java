package data;

import java.sql.*;
import java.time.LocalDate;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import entidades.*;

public class ReservaDAO {
    private static final Logger logger = LoggerFactory.getLogger(ReservaDAO.class);

    public ReservaDAO() {
    }

    public LinkedList<Reserva> getAll() {
        logger.debug("Obteniendo todas las reservas");
        LinkedList<Reserva> reservas = new LinkedList<>();
        
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConnectionDB.getInstancia().getConn();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM reservas");

            while (rs.next()) {
                reservas.add(mapReserva(rs));
            }
            logger.info("Obtenidas {} reservas", reservas.size());
            
        } catch (SQLException e) {
            logger.error("Error al obtener todas las reservas - Estado: {} - Código: {}", 
                        e.getSQLState(), e.getErrorCode(), e);
        } finally {
            closeResources(rs, stmt);
            ConnectionDB.getInstancia().releaseConn();
        }
        return reservas;
    }

    public Reserva getByReserva(int id_reserva) {
        logger.debug("Buscando reserva con ID: {}", id_reserva);
        Reserva reserva = null;
        
        String query = "SELECT r.*, v.id_viaje, v.fecha, v.origen, v.destino " +
                       "FROM reservas r " +
                       "INNER JOIN viajes v ON r.id_viaje = v.id_viaje " +
                       "INNER JOIN usuarios u ON u.id_usuario = r.id_pasajero_reserva " +
                       "WHERE r.id_reserva = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConnectionDB.getInstancia().getConn();
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, id_reserva);

            rs = stmt.executeQuery();
            if (rs.next()) {
                reserva = mapReservaWithViaje(rs); 
                logger.debug("Reserva encontrada: ID {}", id_reserva);
            } else {
                logger.warn("Reserva no encontrada: ID {}", id_reserva);
            }
            
        } catch (SQLException e) {
            logger.error("Error al obtener reserva ID {} - Estado: {} - Código: {}", 
                        id_reserva, e.getSQLState(), e.getErrorCode(), e);
        } finally {
            closeResources(rs, stmt);
            ConnectionDB.getInstancia().releaseConn();
        }
        return reserva;
    }

    public Reserva getByToken(String token) {
        logger.debug("Buscando reserva con token: {}", token);
        Reserva reserva = null;
        
        String query = "SELECT r.*, v.id_viaje, v.fecha, v.origen, v.destino " +
                       "FROM reservas r " +
                       "INNER JOIN viajes v ON r.id_viaje = v.id_viaje " +
                       "INNER JOIN usuarios u ON u.id_usuario = r.id_pasajero_reserva " +
                       "WHERE r.feedback_token = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConnectionDB.getInstancia().getConn();
            stmt = conn.prepareStatement(query);
            stmt.setString(1, token);

            rs = stmt.executeQuery();
            if (rs.next()) {
                reserva = mapReservaWithViaje(rs);
                logger.debug("Reserva encontrada con token: {}", token);
            } else {
                logger.warn("Reserva no encontrada con token: {}", token);
            }
            
        } catch (SQLException e) {
            logger.error("Error al obtener reserva con token {} - Estado: {} - Código: {}", 
                        token, e.getSQLState(), e.getErrorCode(), e);
        } finally {
            closeResources(rs, stmt);
            ConnectionDB.getInstancia().releaseConn();
        }
        return reserva;
    }


    public int obtenerCantidad(int idReserva) {
        logger.debug("Obteniendo cantidad de pasajeros para reserva ID: {}", idReserva);
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConnectionDB.getInstancia().getConn();
            stmt = conn.prepareStatement("SELECT cantidad_pasajeros_reservada FROM reservas WHERE id_reserva = ?");
            stmt.setInt(1, idReserva);

            rs = stmt.executeQuery();
            if (rs.next()) {
                int cantidad = rs.getInt("cantidad_pasajeros_reservada");
                logger.debug("Cantidad obtenida: {} para reserva ID: {}", cantidad, idReserva);
                return cantidad;
            } else {
                logger.warn("Reserva no encontrada: ID {}", idReserva);
                throw new DAOException("Reserva no encontrada con ID: " + idReserva);
            }
            
        } catch (SQLException e) {
            logger.error("Error al obtener cantidad para reserva ID {} - Estado: {} - Código: {}", 
                        idReserva, e.getSQLState(), e.getErrorCode(), e);
            throw new DAOException("Error al obtener cantidad para reserva", e);
        } finally {
            closeResources(rs, stmt);
            ConnectionDB.getInstancia().releaseConn();
        }
    }

    public LinkedList<Reserva> getByUser(Usuario usuario) {
        logger.debug("Obteniendo reservas para usuario ID: {}", usuario.getIdUsuario());
        
        String query = "SELECT " +
                       "r.id_reserva, r.fecha_reserva, r.estado, r.cantidad_pasajeros_reservada, " +
                       "r.reserva_cancelada, r.codigo_reserva, r.feedback_token, " +
                       "v.id_viaje, v.fecha, v.lugares_disponibles, v.origen, v.destino, " +
                       "v.precio_unitario, v.cancelado, v.lugar_salida, v.id_vehiculo_viaje, " +
                       "u_conductor.id_usuario as conductor_id, u_conductor.nombre as conductor_nombre, " +
                       "u_conductor.apellido as conductor_apellido, u_conductor.correo as conductor_correo, " +
                       "u_conductor.telefono as conductor_telefono, " +
                       "u_pasajero.id_usuario as pasajero_id, u_pasajero.nombre as pasajero_nombre, " +
                       "u_pasajero.apellido as pasajero_apellido, u_pasajero.correo as pasajero_correo, " +
                       "u_pasajero.telefono as pasajero_telefono " +
                       "FROM reservas r " +
                       "INNER JOIN viajes v ON r.id_viaje = v.id_viaje " +
                       "INNER JOIN usuarios u_conductor ON u_conductor.id_usuario = v.id_conductor " +
                       "INNER JOIN usuarios u_pasajero ON u_pasajero.id_usuario = r.id_pasajero_reserva " +
                       "WHERE r.id_pasajero_reserva = ? AND r.reserva_cancelada = false";

        validateUsuario(usuario);
        LinkedList<Reserva> reservas = new LinkedList<>();

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionDB.getInstancia().getConn();
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, usuario.getIdUsuario());
            rs = stmt.executeQuery();

            while (rs.next()) {
                reservas.add(mapFullReservaFromJoin(rs));
            }
            logger.info("Encontradas {} reservas para usuario ID: {}", reservas.size(), usuario.getIdUsuario());

        } catch (SQLException e) {
            logger.error("Error al obtener reservas por usuario ID {} - Estado: {} - Código: {}", 
                        usuario.getIdUsuario(), e.getSQLState(), e.getErrorCode(), e);
            throw new DAOException("Error al obtener reservas por usuario", e);
        } finally {
            closeResources(rs, stmt);
            ConnectionDB.getInstancia().releaseConn();
        }
        return reservas;
    }

    public LinkedList<Reserva> getReservasByViaje(int idViaje) {
        logger.debug("Obteniendo reservas para viaje ID: {}", idViaje);
        
        String query = "SELECT " +
                       "r.id_reserva, r.fecha_reserva, r.estado, r.cantidad_pasajeros_reservada, " +
                       "r.reserva_cancelada, r.codigo_reserva, r.feedback_token, " +
                       "v.id_viaje, v.fecha, v.lugares_disponibles, v.origen, v.destino, " +
                       "v.precio_unitario, v.cancelado, v.lugar_salida, v.id_vehiculo_viaje, " +
                       "u_conductor.id_usuario as conductor_id, u_conductor.nombre as conductor_nombre, " +
                       "u_conductor.apellido as conductor_apellido, u_conductor.correo as conductor_correo, " +
                       "u_conductor.telefono as conductor_telefono, " +
                       "u_pasajero.id_usuario as pasajero_id, u_pasajero.nombre as pasajero_nombre, " +
                       "u_pasajero.apellido as pasajero_apellido, u_pasajero.correo as pasajero_correo, " +
                       "u_pasajero.telefono as pasajero_telefono " +
                       "FROM reservas r " +
                       "INNER JOIN viajes v ON r.id_viaje = v.id_viaje " +
                       "INNER JOIN usuarios u_conductor ON u_conductor.id_usuario = v.id_conductor " +
                       "INNER JOIN usuarios u_pasajero ON u_pasajero.id_usuario = r.id_pasajero_reserva " +
                       "WHERE r.id_viaje = ? AND r.reserva_cancelada = false";

        LinkedList<Reserva> reservas = new LinkedList<>();

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionDB.getInstancia().getConn();
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, idViaje);
            rs = stmt.executeQuery();

            while (rs.next()) {
                reservas.add(mapFullReservaFromJoin(rs));
            }
            logger.info("Encontradas {} reservas para viaje ID: {}", reservas.size(), idViaje);

        } catch (SQLException e) {
            logger.error("Error al obtener reservas por viaje ID {} - Estado: {} - Código: {}", 
                        idViaje, e.getSQLState(), e.getErrorCode(), e);
            throw new DAOException("Error al obtener reservas por viaje", e);
        } finally {
            closeResources(rs, stmt);
            ConnectionDB.getInstancia().releaseConn();
        }
        return reservas;
    }

    public LinkedList<Reserva> getReservasForFeedback() {
        logger.debug("Obteniendo reservas para feedback");
        
        LinkedList<Reserva> reservas = new LinkedList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionDB.getInstancia().getConn();
            stmt = conn.prepareStatement(
                "SELECT r.*, v.id_viaje, v.fecha, v.origen, v.destino " +
                "FROM reservas r " +
                "INNER JOIN viajes v ON r.id_viaje = v.id_viaje " +
                "INNER JOIN usuarios u ON u.id_usuario = r.id_pasajero_reserva " +
                "WHERE v.fecha = ? " +
                "AND r.estado = 'CONFIRMADA' " +
                "AND r.reserva_cancelada = false");

            LocalDate ayer = LocalDate.now().minusDays(1);
            stmt.setDate(1, java.sql.Date.valueOf(ayer));

            rs = stmt.executeQuery();
            while (rs.next()) {
                reservas.add(mapReservaWithViaje(rs)); 
            }
            logger.info("Obtenidas {} reservas para feedback", reservas.size());

        } catch (SQLException e) {
            logger.error("Error al obtener reservas para feedback - Estado: {} - Código: {}", 
                        e.getSQLState(), e.getErrorCode(), e);
        } finally {
            closeResources(rs, stmt);
            ConnectionDB.getInstancia().releaseConn();
        }
        return reservas;
    }

    public void add(Reserva reserva) {
        logger.info("Creando nueva reserva para usuario ID: {}", reserva.getPasajero().getIdUsuario());
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet keys = null;
        
        try {
            conn = ConnectionDB.getInstancia().getConn();
            stmt = conn.prepareStatement(
                "INSERT INTO reservas(fecha_reserva, cantidad_pasajeros_reservada, " +
                "reserva_cancelada, id_viaje, id_pasajero_reserva, codigo_reserva, estado) " +
                "VALUES(?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            
            stmt.setString(1, reserva.getFecha_reserva());
            stmt.setInt(2, reserva.getCantidad_pasajeros_reservada());
            stmt.setBoolean(3, reserva.isReserva_cancelada());
            stmt.setInt(4, reserva.getViaje().getIdViaje());
            stmt.setInt(5, reserva.getPasajero().getIdUsuario());
            stmt.setInt(6, reserva.getCodigo_reserva());
            stmt.setString(7, reserva.getEstado());

            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                keys = stmt.getGeneratedKeys();
                if (keys.next()) {
                    reserva.setIdReserva(keys.getInt(1));
                    logger.info("Reserva creada exitosamente: ID {}", reserva.getIdReserva());
                }
            } else {
                logger.error("No se pudo crear la reserva");
            }

        } catch (SQLException e) {
            logger.error("Error al crear reserva - Estado: {} - Código: {}", 
                        e.getSQLState(), e.getErrorCode(), e);
            throw new DAOException("Error al agregar reserva", e);
        } finally {
            closeResources(keys, stmt);
            ConnectionDB.getInstancia().releaseConn();
        }
    }

    public void actualizarEstado(int idReserva, String nuevoEstado) {
        logger.info("Actualizando estado de reserva ID: {} a {}", idReserva, nuevoEstado);
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = ConnectionDB.getInstancia().getConn();
            stmt = conn.prepareStatement("UPDATE reservas SET estado = ? WHERE id_reserva = ?");
            stmt.setString(1, nuevoEstado);
            stmt.setInt(2, idReserva);

            int affected = stmt.executeUpdate();
            checkAffectedRows(affected, "actualizar estado");
            logger.info("Estado de reserva ID {} actualizado a {}", idReserva, nuevoEstado);

        } catch (SQLException e) {
            logger.error("Error al actualizar estado de reserva ID {} - Estado: {} - Código: {}", 
                        idReserva, e.getSQLState(), e.getErrorCode(), e);
            throw new DAOException("Error al actualizar estado de reserva", e);
        } finally {
            closeResources(null, stmt);
            ConnectionDB.getInstancia().releaseConn();
        }
    }

    public void update(Reserva reserva, int idReserva) {
        logger.info("Actualizando reserva ID: {}", idReserva);
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = ConnectionDB.getInstancia().getConn();
            stmt = conn.prepareStatement(
                "UPDATE reservas SET cantidad_pasajeros_reservada = ?, reserva_cancelada = ?, " +
                "id_viaje = ?, id_pasajero_reserva = ? WHERE id_reserva = ?");

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
            throw new DAOException("Error al actualizar reserva", e);
        } finally {
            closeResources(null, stmt);
            ConnectionDB.getInstancia().releaseConn();
        }
    }

    public void guardarToken(int idReserva, String token) {
        logger.info("Guardando token para reserva ID: {}", idReserva);
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = ConnectionDB.getInstancia().getConn();
            stmt = conn.prepareStatement("UPDATE reservas SET feedback_token = ? WHERE id_reserva = ?");
            stmt.setString(1, token);
            stmt.setInt(2, idReserva);

            int affected = stmt.executeUpdate();
            checkAffectedRows(affected, "guardar token");
            logger.info("Token guardado para reserva ID: {}", idReserva);

        } catch (SQLException e) {
            logger.error("Error al guardar token para reserva ID {} - Estado: {} - Código: {}", 
                        idReserva, e.getSQLState(), e.getErrorCode(), e);
            throw new DAOException("Error al guardar token en la reserva", e);
        } finally {
            closeResources(null, stmt);
            ConnectionDB.getInstancia().releaseConn();
        }
    }

    public boolean cancelarReserva(int idReserva) {
        logger.info("Cancelando reserva ID: {}", idReserva);
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = ConnectionDB.getInstancia().getConn();
            stmt = conn.prepareStatement(
                "UPDATE reservas SET reserva_cancelada = true, estado = 'CANCELADA' WHERE id_reserva = ?");
            stmt.setInt(1, idReserva);

            int affected = stmt.executeUpdate();
            checkAffectedRows(affected, "cancelar");
            
            logger.info("Reserva cancelada exitosamente: ID {}", idReserva);
            return true;

        } catch (SQLException e) {
            logger.error("Error al cancelar reserva ID {} - Estado: {} - Código: {}", 
                        idReserva, e.getSQLState(), e.getErrorCode(), e);
            return false;
        } finally {
            closeResources(null, stmt);
            ConnectionDB.getInstancia().releaseConn();
        }
    }

    public void delete(int idReserva) {
        logger.info("Eliminando reserva ID: {}", idReserva);
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = ConnectionDB.getInstancia().getConn();
            stmt = conn.prepareStatement("DELETE FROM reservas WHERE id_reserva = ?");
            stmt.setInt(1, idReserva);

            int affected = stmt.executeUpdate();
            checkAffectedRows(affected, "eliminar");
            logger.info("Reserva eliminada exitosamente: ID {}", idReserva);

        } catch (SQLException e) {
            logger.error("Error al eliminar reserva ID {} - Estado: {} - Código: {}", 
                        idReserva, e.getSQLState(), e.getErrorCode(), e);
            throw new DAOException("Error al eliminar reserva", e);
        } finally {
            closeResources(null, stmt);
            ConnectionDB.getInstancia().releaseConn();
        }
    }

    // ========== MÉTODOS DE MAPEO ==========

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
        vehiculo.setId_vehiculo(rs.getInt("id_vehiculo_viaje"));
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

    private Reserva mapReserva(ResultSet rs) throws SQLException {
        Reserva reserva = new Reserva();
        reserva.setIdReserva(rs.getInt("id_reserva"));
        reserva.setFecha_reserva(rs.getString("fecha_reserva"));
        reserva.setCantidad_pasajeros_reservada(rs.getInt("cantidad_pasajeros_reservada"));
        reserva.setReserva_cancelada(rs.getBoolean("reserva_cancelada"));
        reserva.setEstado(rs.getString("estado"));
        reserva.setCodigo_reserva(rs.getInt("codigo_reserva"));
        reserva.setFeedback_token(rs.getString("feedback_token"));
        
        Usuario pasajero = new Usuario();
        pasajero.setIdUsuario(rs.getInt("id_pasajero_reserva"));
        reserva.setPasajero(pasajero);
        
        Viaje viaje = new Viaje();
        viaje.setIdViaje(rs.getInt("id_viaje"));
        reserva.setViaje(viaje);
        
        return reserva;
    }
    
    /**
     * Mapeo para reservas que necesitan datos básicos del viaje
     */
    private Reserva mapReservaWithViaje(ResultSet rs) throws SQLException {
        Reserva reserva = new Reserva();
        reserva.setIdReserva(rs.getInt("id_reserva"));
        reserva.setFecha_reserva(rs.getString("fecha_reserva"));
        reserva.setCantidad_pasajeros_reservada(rs.getInt("cantidad_pasajeros_reservada"));
        reserva.setReserva_cancelada(rs.getBoolean("reserva_cancelada"));
        reserva.setEstado(rs.getString("estado"));
        reserva.setCodigo_reserva(rs.getInt("codigo_reserva"));
        reserva.setFeedback_token(rs.getString("feedback_token"));
        
        Usuario pasajero = new Usuario();
        pasajero.setIdUsuario(rs.getInt("id_pasajero_reserva"));
        reserva.setPasajero(pasajero);
        
        Viaje viaje = new Viaje();
        viaje.setIdViaje(rs.getInt("id_viaje"));
        viaje.setFecha(rs.getDate("fecha"));
        viaje.setOrigen(rs.getString("origen"));
        viaje.setDestino(rs.getString("destino"));
        reserva.setViaje(viaje);
        
        return reserva;
    }

    // Método auxiliar para verificar si una columna existe en el ResultSet
    private boolean hasColumn(ResultSet rs, String columnName) {
        try {
            rs.findColumn(columnName);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    // ========== MÉTODOS AUXILIARES ==========

    private void validateUsuario(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario inválido");
        }
    }

    private void validateReserva(Reserva reserva) {
        if (reserva == null || reserva.getViaje() == null) {
            throw new IllegalArgumentException("Reserva inválida");
        }
    }

    private void checkAffectedRows(int affected, String operation) {
        if (affected == 0) {
            throw new DAOException("Ningún registro afectado al " + operation);
        }
    }

    private void closeResources(ResultSet rs, Statement stmt) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
        } catch (SQLException e) {
            logger.error("Error cerrando recursos", e);
        }
    }

    // ========== EXCEPCIÓN PERSONALIZADA ==========

    public static class DAOException extends RuntimeException {
        public DAOException(String message) {
            super(message);
        }

        public DAOException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}