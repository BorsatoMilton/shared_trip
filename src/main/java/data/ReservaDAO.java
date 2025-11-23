package data;

import java.sql.*;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import entidades.*;

public class ReservaDAO {
    private static final Logger logger = LoggerFactory.getLogger(ReservaDAO.class);
    private final ViajeDAO viajeDAO;
    private final UserDAO usuarioDAO;
    private final VehiculoDAO vehiculoDAO;

    public ReservaDAO() {
        this.viajeDAO = new ViajeDAO();
        this.usuarioDAO = new UserDAO();
        this.vehiculoDAO = new VehiculoDAO();
    }

    public LinkedList<Reserva> getAll() {
        LinkedList<Reserva> reservas = new LinkedList<>();
        String query = "SELECT r.*, v.*, u.* FROM reservas r "
                + "INNER JOIN viajes v ON r.id_viaje = v.id_viaje "
                + "INNER JOIN usuarios u ON v.id_conductor = u.id_usuario";
        try (Connection conn = ConnectionDB.getInstancia().getConn();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                reservas.add(mapFullReserva(rs));
            }
            logger.debug("Obtenidas {} reservas", reservas.size());
        } catch (SQLException e) {
            handleSQLException("Error al obtener reservas", e);
        }
        return reservas;
    }

    public Reserva getByReserva(int id_reserva) {
        Reserva reserva = null;
        String query = "SELECT r.*, v.* FROM reservas r "
                + "INNER JOIN viajes v ON r.id_viaje = v.id_viaje WHERE r.idReserva = ?";
        Connection conn = null;

        try {
            conn = ConnectionDB.getInstancia().getConn();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, id_reserva);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        reserva = mapReserva(rs);
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error al obtener Reserva con ID: {}", id_reserva, e);
        } finally {
            ConnectionDB.getInstancia().releaseConn();
        }
        return reserva;
    }


    public Reserva getByToken(String token) {
        Reserva reserva = null;
        String query = "SELECT r.* FROM reservas r WHERE r.feedback_token = ?";
        Connection conn = null;

        try {
            conn = ConnectionDB.getInstancia().getConn();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, token);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        reserva = mapReserva(rs);
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error al obtener Reserva con token: {}", token, e);
        } finally {
            ConnectionDB.getInstancia().releaseConn();
        }
        return reserva;
    }

    public int obtenerCantidad(int idReserva) {

        String query = "SELECT cantidad_pasajeros_reservada FROM reservas WHERE idReserva = ?";


        try (Connection conn = ConnectionDB.getInstancia().getConn();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idReserva);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("cantidad_pasajeros_reservada");
                }
                throw new DAOException("Reserva no encontrada con ID: " + idReserva);
            }

        } catch (SQLException e) {
            return handleSQLException("Error al obtener cantidad para reserva " + idReserva, e);
        } finally {
            ConnectionDB.getInstancia().releaseConn();
        }
    }

    public LinkedList<Reserva> getByUser(Usuario usuario) {
        String query = "SELECT r.id_pasajero_reserva, r.idReserva, r.fecha_reserva, r.estado, " +
                       "r.cantidad_pasajeros_reservada, r.reserva_cancelada, r.codigo_reserva, " +
                       "v.id_viaje, v.origen, v.destino, v.fecha, v.lugares_disponibles, v.precio_unitario, " +
                       "v.id_vehiculo_viaje, " +
                       "u.id_usuario, u.nombre, u.apellido, u.correo, u.telefono " +  // ← QUITÉ LA COMA EXTRA
                       "FROM reservas r " +
                       "INNER JOIN viajes v ON r.id_viaje = v.id_viaje " +
                       "INNER JOIN usuarios u ON u.id_usuario = v.id_conductor " +
                       "WHERE r.id_pasajero_reserva = ? AND r.reserva_cancelada = false";


        validateUsuario(usuario);
        LinkedList<Reserva> reservas = new LinkedList<>();

        try (Connection conn = ConnectionDB.getInstancia().getConn();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, usuario.getIdUsuario());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reservas.add(mapFullReserva(rs));
                }
            }
            logger.debug("Encontradas {} reservas para usuario {}", reservas.size(), usuario.getIdUsuario());

        } catch (SQLException e) {
            handleSQLException("Error al obtener reservas por usuario", e);
        } finally {
            ConnectionDB.getInstancia().releaseConn();
        }
        return reservas;
    }

    public LinkedList<Reserva> getReservasByViaje(int idViaje) {
        String query = "SELECT r.id_pasajero_reserva, r.idReserva, r.fecha_reserva, r.estado ,r.cantidad_pasajeros_reservada, r.reserva_cancelada, r.codigo_reserva, "
                + "v.id_viaje, v.origen, v.destino, v.fecha, v.lugares_disponibles, v.precio_unitario, "
                + "u.id_usuario, u.nombre, u.apellido, u.correo, u.telefono "
                + "FROM reservas r "
                + "INNER JOIN viajes v ON r.id_viaje = v.id_viaje "
                + "INNER JOIN usuarios u ON u.id_usuario = v.id_conductor "
                + "WHERE r.id_viaje = ? AND r.reserva_cancelada = false";


        LinkedList<Reserva> reservas = new LinkedList<>();

        try (Connection conn = ConnectionDB.getInstancia().getConn();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idViaje);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reservas.add(mapFullReserva(rs));
                }
            }
            logger.debug("Encontradas {} reservas el viaje {}", reservas.size(), idViaje);

        } catch (SQLException e) {
            handleSQLException("Error al obtener reservas por viaje", e);
        } finally {
            ConnectionDB.getInstancia().releaseConn();
        }
        return reservas;
    }

    public LinkedList<Reserva> getReservasForFeedback() {

        LinkedList<Reserva> reservas = new LinkedList<>();

        String query = "SELECT r.*, v.*, u.* FROM reservas r "
                + "INNER JOIN viajes v ON r.id_viaje = v.id_viaje "
                + "INNER JOIN usuarios u ON v.id_conductor = u.id_usuario "
                + "WHERE v.fecha = ? "
                + "AND r.estado = 'CONFIRMADA' "
                + "AND r.reserva_cancelada = false";

        try (Connection conn = ConnectionDB.getInstancia().getConn();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            LocalDate ayer = LocalDate.now().minusDays(1);
            java.sql.Date ayerSQL = java.sql.Date.valueOf(ayer);

            stmt.setDate(1, ayerSQL);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reservas.add(mapFullReserva(rs));
                }
            }

            logger.debug("Obtenidas {} reservas para feedback", reservas.size());

        } catch (SQLException e) {
            handleSQLException("Error al obtener reservas para feedback", e);
        }

        return reservas;
    }


    public void add(Reserva reserva) {

        String query = "INSERT INTO reservas(fecha_reserva, cantidad_pasajeros_reservada, "
                + "reserva_cancelada, id_viaje, id_pasajero_reserva, codigo_reserva, estado) VALUES(?,?,?,?,?,?,?)";

        validateReserva(reserva);

        try (Connection conn = ConnectionDB.getInstancia().getConn();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            setInsertParameters(stmt, reserva);
            stmt.executeUpdate();
            setGeneratedId(stmt, reserva);

            logger.info("Reserva agregada ID: {}", reserva.getIdReserva());

        } catch (SQLException e) {
            handleSQLException("Error al agregar reserva", e);
        } finally {
            ConnectionDB.getInstancia().releaseConn();
        }
    }

    public void actualizarEstado(int idReserva, String nuevoEstado) {
        String query = "UPDATE reservas SET estado = ? WHERE idReserva = ?";

        try (Connection conn = ConnectionDB.getInstancia().getConn();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, nuevoEstado);
            stmt.setInt(2, idReserva);

            int affected = stmt.executeUpdate();
            checkAffectedRows(affected, "actualizar estado");

        } catch (SQLException e) {
            handleSQLException("Error al actualizar estado de reserva " + idReserva, e);
        } finally {
            ConnectionDB.getInstancia().releaseConn();
        }
    }

    public void update(Reserva reserva, int idReserva) {

        String query = "UPDATE reservas SET cantidad_pasajeros_reservada = ?, reserva_cancelada = ?, "
                + "id_viaje = ?, id_pasajero_reserva = ? WHERE idReserva = ?";

        validateReserva(reserva);

        try (Connection conn = ConnectionDB.getInstancia().getConn();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            setUpdateParameters(stmt, reserva, idReserva);
            int affected = stmt.executeUpdate();
            checkAffectedRows(affected, "actualizar");

            logger.info("Reserva actualizada ID: {}", idReserva);

        } catch (SQLException e) {
            handleSQLException("Error al actualizar reserva " + idReserva, e);
        } finally {
            ConnectionDB.getInstancia().releaseConn();
        }
    }

    public void guardarToken(int idReserva, String token) throws SQLException {
        String sql = "UPDATE reservas SET feedback_token = ? WHERE idReserva = ?";
        try (Connection conn = ConnectionDB.getInstancia().getConn();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, token);
            stmt.setInt(2, idReserva);
            stmt.executeUpdate();

            int affected = stmt.executeUpdate();
            checkAffectedRows(affected, "actualizar");

            logger.info("Reserva actualizada ID: {}", idReserva);
        } catch (SQLException e) {
        handleSQLException("Error al guardar el token en la reserva " + idReserva, e);
        } finally {
            ConnectionDB.getInstancia().releaseConn();
        }

    }


    public boolean cancelarReserva(int idReserva) {

        String query = "UPDATE reservas SET reserva_cancelada = true, estado = 'CANCELADA' WHERE idReserva = ?";

        try (Connection conn = ConnectionDB.getInstancia().getConn();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idReserva);
            int affected = stmt.executeUpdate();
            checkAffectedRows(affected, "cancelar");
            if (affected == 0) {
                return false;
            }
            logger.info("Reserva cancelada ID: {}", idReserva);
            return true;

        } catch (SQLException e) {
            handleSQLException("Error al cancelar reserva " + idReserva, e);
            return false;
        } finally {
            ConnectionDB.getInstancia().releaseConn();
        }
    }

    public void delete(int idReserva) {

        String query = "DELETE FROM reservas WHERE idReserva = ?";


        try (Connection conn = ConnectionDB.getInstancia().getConn();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idReserva);
            int affected = stmt.executeUpdate();
            checkAffectedRows(affected, "eliminar");

            logger.info("Reserva eliminada ID: {}", idReserva);

        } catch (SQLException e) {
            handleSQLException("Error al eliminar reserva " + idReserva, e);
        } finally {
            ConnectionDB.getInstancia().releaseConn();
        }
    }

    // Métodos auxiliares
    private Reserva mapReserva(ResultSet rs) throws SQLException {
        Reserva reserva = new Reserva();
        reserva.setIdReserva(rs.getInt("idReserva"));
        reserva.setFecha_reserva(rs.getString("fecha_reserva"));
        reserva.setCantidad_pasajeros_reservada(rs.getInt("cantidad_pasajeros_reservada"));
        reserva.setReserva_cancelada(rs.getBoolean("reserva_cancelada"));
        reserva.setPasajero(usuarioDAO.getById(rs.getInt("id_pasajero_reserva")));
        reserva.setViaje(viajeDAO.getByViaje(rs.getInt("id_viaje")));
        reserva.setEstado(rs.getString("estado"));
        reserva.setCodigo_reserva(rs.getInt("codigo_reserva"));

        return reserva;
    }

    private Reserva mapFullReserva(ResultSet rs) throws SQLException {
        Reserva reserva = mapReserva(rs);
        reserva.setViaje(mapViaje(rs));
        return reserva;
    }

    private Viaje mapViaje(ResultSet rs) throws SQLException {
        Viaje viaje = new Viaje();
        viaje.setIdViaje(rs.getInt("id_viaje"));
        viaje.setOrigen(rs.getString("origen"));
        viaje.setDestino(rs.getString("destino"));
        viaje.setFecha(rs.getDate("fecha"));
        viaje.setLugares_disponibles(rs.getInt("lugares_disponibles"));
        viaje.setPrecio_unitario(rs.getDouble("precio_unitario"));
        viaje.setVehiculo(vehiculoDAO.getById_vehiculo(rs.getInt("id_vehiculo_viaje")));
        viaje.setConductor(mapConductor(rs));
        return viaje;
    }

    private Usuario mapConductor(ResultSet rs) throws SQLException {
        Usuario conductor = new Usuario();
        conductor.setIdUsuario(rs.getInt("id_usuario"));
        conductor.setNombre(rs.getString("nombre"));
        conductor.setApellido(rs.getString("apellido"));
        conductor.setCorreo(rs.getString("correo"));
        conductor.setTelefono(rs.getString("telefono"));
        return conductor;
    }

    private void setInsertParameters(PreparedStatement stmt, Reserva r) throws SQLException {
        stmt.setString(1, r.getFecha_reserva());
        stmt.setInt(2, r.getCantidad_pasajeros_reservada());
        stmt.setBoolean(3, r.isReserva_cancelada());
        stmt.setInt(4, r.getViaje().getIdViaje());
        stmt.setInt(5, r.getPasajero().getIdUsuario());
        stmt.setInt(6, r.getCodigo_reserva());
        stmt.setString(7, r.getEstado());
    }


    private void setUpdateParameters(PreparedStatement stmt, Reserva r, int id_reserva) throws SQLException {
        stmt.setInt(1, r.getCantidad_pasajeros_reservada());
        stmt.setBoolean(2, r.isReserva_cancelada());
        stmt.setInt(3, r.getViaje().getIdViaje());
        stmt.setInt(4, r.getPasajero().getIdUsuario());
        stmt.setInt(5, id_reserva);
    }

    private void setGeneratedId(PreparedStatement stmt, Reserva r) throws SQLException {
        try (ResultSet keys = stmt.getGeneratedKeys()) {
            if (keys.next()) {
                r.setIdReserva(keys.getInt(1));
            }
        }
    }

    // Validaciones

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

    // Manejo de excepciones
    private int handleSQLException(String message, SQLException e) throws DAOException {
        logger.error("{} - Error Code: {} - SQL State: {}", message, e.getErrorCode(), e.getSQLState(), e);
        throw new DAOException(message, e);
    }

    // Excepción personalizada
    public static class DAOException extends RuntimeException {
        public DAOException(String message) {
            super(message);
        }

        public DAOException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}