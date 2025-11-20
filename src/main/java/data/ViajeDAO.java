package data;

import java.sql.*;
import java.time.LocalDate;
import java.util.LinkedList;

import entidades.Vehiculo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import entidades.Usuario;
import entidades.Viaje;

public class ViajeDAO {

    private static final Logger logger = LoggerFactory.getLogger(ViajeDAO.class);

    public LinkedList<Viaje> getAll() {
        LinkedList<Viaje> viajes = new LinkedList<>();
        String query = "SELECT v.* FROM viajes v "
                + "INNER JOIN usuarios u ON u.id_usuario = v.id_conductor "
                + "WHERE v.fecha >= CURRENT_DATE AND u.fecha_baja IS NULL AND v.cancelado = 0";
        Connection conn = null;

        try {
            conn = ConnectionDB.getInstancia().getConn();
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {

                while (rs.next()) {
                    viajes.add(mapViaje(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error al obtener todos los viajes", e);
        } finally {
            ConnectionDB.getInstancia().releaseConn();
        }
        return viajes;
    }

    public LinkedList<Viaje> getAllBySearch(String origen, String destino, String fecha) {
        LinkedList<Viaje> viajes = new LinkedList<>();
        String query = "SELECT * FROM viajes WHERE origen = ? AND destino = ? AND fecha = ?";
        Connection conn = null;

        try {
            conn = ConnectionDB.getInstancia().getConn();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, origen);
                stmt.setString(2, destino);

                java.sql.Date fechaSql;
                if (fecha != null && !fecha.isEmpty()) {
                    try {
                        fechaSql = java.sql.Date.valueOf(fecha);
                    } catch (IllegalArgumentException e) {
                        logger.error("Formato de fecha inválido: {}. Usando fecha actual.", fecha, e);
                        fechaSql = java.sql.Date.valueOf(LocalDate.now());
                    }
                } else {
                    fechaSql = java.sql.Date.valueOf(LocalDate.now());
                }
                stmt.setDate(3, fechaSql);

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        viajes.add(mapViaje(rs));
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error al buscar viajes por origen: {}, destino: {}, fecha: {}", origen, destino, fecha, e);
        } finally {
            ConnectionDB.getInstancia().releaseConn();
        }
        return viajes;
    }

    public Viaje getByViaje(int id_viaje) {
        Viaje viaje = null;
        String query = "SELECT * FROM viajes WHERE id_viaje = ?";
        Connection conn = null;

        try {
            conn = ConnectionDB.getInstancia().getConn();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, id_viaje);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        viaje = mapViaje(rs);
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error al obtener viaje con ID: {}", id_viaje, e);
        } finally {
            ConnectionDB.getInstancia().releaseConn();
        }
        return viaje;
    }

    public LinkedList<Viaje> getByUser(Usuario u) {
        LinkedList<Viaje> viajes = new LinkedList<>();
        String query = "SELECT * FROM viajes WHERE id_conductor = ?";
        Connection conn = null;

        try {
            conn = ConnectionDB.getInstancia().getConn();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, u.getIdUsuario());

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        viajes.add(mapViaje(rs));
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error al obtener viajes del usuario con ID: {}", u.getIdUsuario(), e);
        } finally {
            ConnectionDB.getInstancia().releaseConn();
        }
        return viajes;
    }

    /*public boolean existeViajeEnFecha(int idConductor, int idVehiculo, Date fecha){
        String sql = "SELECT COUNT(*) AS total FROM viajes WHERE id_conductor = ? AND id_vehiculo_viaje = ? AND fecha = ? AND cancelado = 0";

        try (PreparedStatement stmt = ConnectionDB.getInstancia().getConn().prepareStatement(sql)) {

            stmt.setInt(1, idConductor);
            stmt.setInt(2, idVehiculo);
            stmt.setDate(3, fecha);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total") > 0;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionDB.getInstancia().releaseConn();
        }

        return false;
    }*/


    public void updateCantidad(int idViaje, int cantPasajeros) {
        String query = "UPDATE viajes SET lugares_disponibles = ? WHERE id_viaje = ?";
        Connection conn = null;

        try {
            conn = ConnectionDB.getInstancia().getConn();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, cantPasajeros);
                stmt.setInt(2, idViaje);

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    logger.info("Lugares actualizados para viaje ID: {}", idViaje);
                } else {
                    logger.warn("No se encontró viaje con ID: {}", idViaje);
                }
            }
        } catch (SQLException e) {
            logger.error("Error al actualizar lugares del viaje ID: {}", idViaje, e);
        } finally {
            ConnectionDB.getInstancia().releaseConn();
        }
    }

    public boolean cancelarViaje(int id_viaje) {
        String query = "UPDATE viajes SET cancelado = true WHERE id_viaje = ?";
        Connection conn = null;
        boolean cancelada = false;

        try {
            conn = ConnectionDB.getInstancia().getConn();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, id_viaje);

                int rowsAffected = stmt.executeUpdate();
                cancelada = rowsAffected > 0;
                if (cancelada) {
                    logger.info("Viaje ID: {} cancelado", id_viaje);
                } else {
                    logger.warn("No se pudo cancelar el viaje ID: {}", id_viaje);
                }
            }
        } catch (SQLException e) {
            logger.error("Error al cancelar viaje ID: {}", id_viaje, e);
        } finally {
            ConnectionDB.getInstancia().releaseConn();
        }
        return cancelada;
    }

    public void update(Viaje v, int id_viaje) {
        String query = "UPDATE viajes SET fecha=?, lugares_disponibles=?, origen=?, destino=?, "
                + "precio_unitario=?, cancelado=?, id_conductor=?, lugar_salida=?, id_vehiculo_viaje=? WHERE id_viaje=?";
        Connection conn = null;

        try {
            conn = ConnectionDB.getInstancia().getConn();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
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
                    logger.info("Viaje ID: {} actualizado exitosamente", id_viaje);
                } else {
                    logger.warn("No se encontró viaje con ID: {}", id_viaje);
                }
            }
        } catch (SQLException e) {
            logger.error("Error al actualizar viaje ID: {}", id_viaje, e);
        } finally {
            ConnectionDB.getInstancia().releaseConn();
        }
    }

    public void add(Viaje v) {
        String query = "INSERT INTO viajes(fecha, lugares_disponibles, origen, destino, precio_unitario, "
                + "cancelado, id_conductor, lugar_salida, codigo_validacion, id_vehiculo_viaje) VALUES (?,?,?,?,?,?,?,?,?,?)";
        Connection conn = null;

        try {
            conn = ConnectionDB.getInstancia().getConn();
            try (PreparedStatement stmt = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
                stmt.setDate(1, v.getFecha());
                stmt.setInt(2, v.getLugares_disponibles());
                stmt.setString(3, v.getOrigen());
                stmt.setString(4, v.getDestino());
                stmt.setDouble(5, v.getPrecio_unitario());
                stmt.setBoolean(6, false);
                stmt.setInt(7, v.getConductor().getIdUsuario());
                stmt.setString(8, v.getLugar_salida());
                stmt.setInt(9, v.getCodigoValidacion());
                stmt.setInt(10, v.getVehiculo().getId_vehiculo());

                int affectedRows = stmt.executeUpdate();
                if (affectedRows > 0) {
                    try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            v.setIdViaje(generatedKeys.getInt(1));
                            logger.info("Viaje creado con ID: {}", v.getIdViaje());
                        }
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error al crear nuevo viaje", e);
        } finally {
            ConnectionDB.getInstancia().releaseConn();
        }
    }

    public void delete(Viaje v) {
        String query = "DELETE FROM viajes WHERE id_viaje = ?";
        Connection conn = null;

        try {
            conn = ConnectionDB.getInstancia().getConn();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, v.getIdViaje());
                int rowsAffected = stmt.executeUpdate();

                if (rowsAffected > 0) {
                    logger.info("Viaje ID: {} eliminado correctamente", v.getIdViaje());
                } else {
                    logger.warn("No se encontró viaje con ID: {}", v.getIdViaje());
                }
            }
        } catch (SQLException e) {
            logger.error("Error al eliminar viaje ID: {}", v.getIdViaje(), e);
        } finally {
            ConnectionDB.getInstancia().releaseConn();
        }
    }

    private Viaje mapViaje(ResultSet rs) throws SQLException {
        Viaje v = new Viaje();
        v.setIdViaje(rs.getInt("id_viaje"));
        v.setFecha(rs.getDate("fecha"));
        v.setLugares_disponibles(rs.getInt("lugares_disponibles"));
        v.setOrigen(rs.getString("origen"));
        v.setDestino(rs.getString("destino"));
        v.setPrecio_unitario(rs.getDouble("precio_unitario"));
        v.setCancelado(rs.getBoolean("cancelado"));
        v.setLugar_salida(rs.getString("lugar_salida"));
        v.setCodigoValidacion(rs.getInt("codigo_validacion"));

        VehiculoDAO vDAO = new VehiculoDAO();
        Vehiculo veh = vDAO.getById_vehiculo(rs.getInt("id_vehiculo_viaje"));
        v.setVehiculo(veh);

        UserDAO usuarioDAO = new UserDAO();
        Usuario conductor = usuarioDAO.getById(rs.getInt("id_conductor"));
        v.setConductor(conductor);

        return v;
    }
}