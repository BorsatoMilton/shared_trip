package data;

import data.exceptions.DataAccessException;
import entities.Rol;
import entities.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.Generators;

import java.sql.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class UserDAO {
    private static final Logger logger = LoggerFactory.getLogger(UserDAO.class);

    public LinkedList<Usuario> getAll() {
        logger.debug("Obteniendo todos los usuarios");
        LinkedList<Usuario> users = new LinkedList<>();

        String query = "SELECT id_usuario, usuario, nombre, apellido, correo, telefono, id_rol " +
                "FROM usuarios WHERE fecha_baja IS NULL";

        try (
                Connection conn = ConnectionDB.getInstancia().getConn();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)
        ) {
            while (rs.next()) {
                users.add(mapUsuario(rs));
            }
            logger.info("Obtenidos {} usuarios", users.size());
            return users;

        } catch (SQLException e) {
            logger.error("Error al obtener todos los usuarios - Estado: {} - Código: {}",
                    e.getSQLState(), e.getErrorCode(), e);
            throw new DataAccessException("Error al obtener todos los usuarios", e);
        }
    }

    public Usuario login(Usuario user) {
        logger.debug("Intentando login para usuario: {}", user.getUsuario());

        String sql = "SELECT u.id_usuario, u.usuario, u.nombre AS nombre, u.apellido, " +
                "u.correo, u.telefono, u.id_rol, u.clave, r.nombre AS nombre_rol " +
                "FROM usuarios u " +
                "INNER JOIN roles r ON r.id_rol = u.id_rol " +
                "WHERE u.usuario = ? AND u.fecha_baja IS NULL";

        try (
                Connection conn = ConnectionDB.getInstancia().getConn();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setString(1, user.getUsuario().trim());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("clave");
                    if (Generators.checkPassword(user.getClave(), storedHash)) {
                        logger.info("Usuario logueado exitosamente: {}", user.getUsuario());
                        return mapUsuarioWithRol(rs);
                    } else {
                        logger.warn("Login fallido para usuario: {}", user.getUsuario());
                        return null;
                    }
                } else {
                    logger.warn("Usuario no encontrado: {}", user.getUsuario());
                    return null;
                }
            }

        } catch (SQLException e) {
            logger.error("Error al realizar login para usuario {} - Estado: {} - Código: {}",
                    user.getUsuario(), e.getSQLState(), e.getErrorCode(), e);
            throw new DataAccessException("Error al realizar login", e);
        }
    }

    public Usuario getById(int id_usuario) {
        logger.debug("Buscando usuario con ID: {}", id_usuario);

        String query = "SELECT id_usuario, usuario, nombre, apellido, correo, telefono, id_rol " +
                "FROM usuarios WHERE id_usuario = ? AND fecha_baja IS NULL";

        try (
                Connection conn = ConnectionDB.getInstancia().getConn();
                PreparedStatement stmt = conn.prepareStatement(query)
        ) {
            stmt.setInt(1, id_usuario);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    logger.debug("Usuario encontrado: ID {}", id_usuario);
                    return mapUsuario(rs);
                } else {
                    logger.warn("Usuario no encontrado: ID {}", id_usuario);
                    return null;
                }
            }

        } catch (SQLException e) {
            logger.error("Error al obtener usuario ID {} - Estado: {} - Código: {}",
                    id_usuario, e.getSQLState(), e.getErrorCode(), e);
            throw new DataAccessException("Error al obtener usuario por ID", e);
        }
    }

    public Usuario getOneUserByEmail(String correo) {
        logger.debug("Buscando usuario por email: {}", correo);

        String query = "SELECT id_usuario, usuario, nombre, apellido, correo, telefono, id_rol " +
                "FROM usuarios WHERE correo = ? AND fecha_baja IS NULL";

        try (
                Connection conn = ConnectionDB.getInstancia().getConn();
                PreparedStatement stmt = conn.prepareStatement(query)
        ) {
            stmt.setString(1, correo);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    logger.debug("Usuario encontrado por email: {}", correo);
                    return mapUsuario(rs);
                } else {
                    logger.warn("Usuario no encontrado por email: {}", correo);
                    return null;
                }
            }

        } catch (SQLException e) {
            logger.error("Error al obtener usuario por email {} - Estado: {} - Código: {}",
                    correo, e.getSQLState(), e.getErrorCode(), e);
            throw new DataAccessException("Error al obtener usuario por email", e);
        }
    }

    public Usuario getOneByUserOrEmail(String user, String correo, Integer idExcluir) {
        logger.debug("Buscando usuario por user: {} o email: {}", user, correo);

        String query = "SELECT id_usuario, usuario, nombre, apellido, correo, telefono, id_rol " +
                "FROM usuarios WHERE (usuario = ? OR correo = ?) AND fecha_baja IS NULL";


        if (idExcluir != null) {
            query += " AND id_usuario <> ?";
        }

        try (
                Connection conn = ConnectionDB.getInstancia().getConn();
                PreparedStatement stmt = conn.prepareStatement(query)
        ) {
            stmt.setString(1, user);
            stmt.setString(2, correo);

            if (idExcluir != null) {
                stmt.setInt(3, idExcluir);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    logger.debug("Usuario encontrado por user/email: {}/{}", user, correo);
                    return mapUsuario(rs);
                } else {
                    logger.debug("Usuario no encontrado por user/email: {}/{}", user, correo);
                    return null;
                }
            }

        } catch (SQLException e) {
            logger.error("Error al obtener usuario por user/email {}/{} - Estado: {} - Código: {}",
                    user, correo, e.getSQLState(), e.getErrorCode(), e);
            throw new DataAccessException("Error al obtener usuario por user/email", e);
        }
    }

    public boolean add(Usuario u) {
        logger.info("Creando nuevo usuario: {}", u.getUsuario());

        String query = "INSERT INTO usuarios(usuario, clave, nombre, apellido, correo, telefono, id_rol) " +
                "VALUES(?,?,?,?,?,?,?)";

        try (
                Connection conn = ConnectionDB.getInstancia().getConn();
                PreparedStatement stmt = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            stmt.setString(1, u.getUsuario());
            stmt.setString(2, u.getClave());
            stmt.setString(3, u.getNombre());
            stmt.setString(4, u.getApellido());
            stmt.setString(5, u.getCorreo());
            stmt.setString(6, u.getTelefono());
            stmt.setInt(7, u.getRol().getIdRol());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        u.setIdUsuario(keys.getInt(1));
                        logger.info("Usuario creado exitosamente: {} (ID: {})", u.getUsuario(), u.getIdUsuario());
                        return true;
                    }
                }
            }
            logger.error("No se pudo crear el usuario: {}", u.getUsuario());
            return false;

        } catch (SQLException e) {
            logger.error("Error al crear usuario {} - Estado: {} - Código: {}",
                    u.getUsuario(), e.getSQLState(), e.getErrorCode(), e);
            throw new DataAccessException("Error al crear usuario", e);
        }
    }

    public boolean update(Usuario u) {
        logger.info("Actualizando usuario ID: {}", u.getIdUsuario());

        String query = "UPDATE usuarios SET usuario = ?, nombre = ?, apellido = ?, correo = ?, telefono = ?, id_rol = ? " +
                "WHERE id_usuario = ?";

        try (
                Connection conn = ConnectionDB.getInstancia().getConn();
                PreparedStatement stmt = conn.prepareStatement(query)
        ) {
            stmt.setString(1, u.getUsuario());
            stmt.setString(2, u.getNombre());
            stmt.setString(3, u.getApellido());
            stmt.setString(4, u.getCorreo());
            stmt.setString(5, u.getTelefono());
            stmt.setInt(6, u.getRol().getIdRol());
            stmt.setInt(7, u.getIdUsuario());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Usuario actualizado exitosamente: ID {}", u.getIdUsuario());
                return true;
            } else {
                logger.warn("No se encontró usuario con ID: {}", u.getIdUsuario());
                return false;
            }

        } catch (SQLException e) {
            logger.error("Error al actualizar usuario ID {} - Estado: {} - Código: {}",
                    u.getIdUsuario(), e.getSQLState(), e.getErrorCode(), e);
            throw new DataAccessException("Error al actualizar usuario", e);
        }
    }

    public boolean updatePassword(int id, String clave) {
        logger.info("Actualizando contraseña para usuario ID: {}", id);

        String query = "UPDATE usuarios SET clave = ? WHERE id_usuario = ?";

        try (
                Connection conn = ConnectionDB.getInstancia().getConn();
                PreparedStatement stmt = conn.prepareStatement(query)
        ) {
            stmt.setString(1, clave);
            stmt.setInt(2, id);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Contraseña actualizada exitosamente para usuario ID: {}", id);
                return true;
            } else {
                logger.warn("No se encontró usuario con ID: {}", id);
                return false;
            }

        } catch (SQLException e) {
            logger.error("Error al actualizar contraseña para usuario ID {} - Estado: {} - Código: {}",
                    id, e.getSQLState(), e.getErrorCode(), e);
            throw new DataAccessException("Error al actualizar contraseña", e);
        }
    }

    public boolean eliminarUsuario(int idUsuario) {
        logger.info("Eliminando usuario ID: {}", idUsuario);

        String query = "UPDATE usuarios SET fecha_baja = current_date WHERE id_usuario = ?";

        try (
                Connection conn = ConnectionDB.getInstancia().getConn();
                PreparedStatement stmt = conn.prepareStatement(query)
        ) {
            stmt.setInt(1, idUsuario);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                logger.info("Usuario eliminado exitosamente: ID {}", idUsuario);
                return true;
            } else {
                logger.warn("No se encontró usuario con ID: {}", idUsuario);
                return false;
            }

        } catch (SQLException e) {
            logger.error("Error al eliminar usuario ID {} - Estado: {} - Código: {}",
                    idUsuario, e.getSQLState(), e.getErrorCode(), e);
            throw new DataAccessException("Error al eliminar usuario", e);
        }
    }

    public int contarAdmins() {
        logger.debug("Contando administradores activos");

        String query = "SELECT COUNT(*) FROM usuarios WHERE id_rol = 1 AND fecha_baja IS NULL";

        try (
                Connection conn = ConnectionDB.getInstancia().getConn();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()
        ) {
            if (rs.next()) {
                int count = rs.getInt(1);
                logger.debug("Total de administradores: {}", count);
                return count;
            }
            return 0;

        } catch (SQLException e) {
            logger.error("Error al contar administradores - Estado: {} - Código: {}",
                    e.getSQLState(), e.getErrorCode(), e);
            throw new DataAccessException("Error al contar administradores", e);
        }
    }

    public boolean tieneViajesActivos(int idUsuario) {
        logger.debug("Verificando si usuario ID {} tiene viajes activos", idUsuario);

        String query = "SELECT COUNT(*) AS total " +
                "FROM viajes " +
                "WHERE id_conductor = ? " +
                "AND fecha > CURRENT_DATE " +
                "AND cancelado = 0";

        try (
                Connection conn = ConnectionDB.getInstancia().getConn();
                PreparedStatement stmt = conn.prepareStatement(query)
        ) {
            stmt.setInt(1, idUsuario);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    boolean tieneViajes = rs.getInt("total") > 0;
                    logger.debug("Usuario ID {} - Tiene viajes activos: {}", idUsuario, tieneViajes);
                    return tieneViajes;
                }
                return false;
            }

        } catch (SQLException e) {
            logger.error("Error al verificar viajes activos para usuario ID {} - Estado: {} - Código: {}",
                    idUsuario, e.getSQLState(), e.getErrorCode(), e);
            throw new DataAccessException("Error al verificar viajes activos del usuario", e);
        }
    }

    public boolean tieneReservasActivas(int idUsuario) {
        logger.debug("Verificando si usuario ID {} tiene reservas activas", idUsuario);

        String query = "SELECT COUNT(*) as total FROM reservas WHERE id_pasajero_reserva = ? AND estado <> 'EN PROCESO'";

        try (
                Connection conn = ConnectionDB.getInstancia().getConn();
                PreparedStatement stmt = conn.prepareStatement(query)
        ) {
            stmt.setInt(1, idUsuario);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    boolean tieneReservas = rs.getInt("total") > 0;
                    logger.debug("Usuario ID {} - Tiene reservas activas: {}", idUsuario, tieneReservas);
                    return tieneReservas;
                }
                return false;
            }

        } catch (SQLException e) {
            logger.error("Error al verificar reservas activas para usuario ID {} - Estado: {} - Código: {}",
                    idUsuario, e.getSQLState(), e.getErrorCode(), e);
            throw new DataAccessException("Error al verificar reservas activas del usuario", e);
        }
    }

    public Map<String, Integer> obtenerEstadisticasUsuarios() {
        logger.debug("Obteniendo estadísticas detalladas de usuarios");

        String query = "SELECT " +
                "r.nombre as rol_base, " +
                "COUNT(*) as total_por_rol, " +
                "SUM(CASE WHEN EXISTS (SELECT 1 FROM viajes v WHERE v.id_conductor = u.id_usuario) THEN 1 ELSE 0 END) as conductores, " +
                "SUM(CASE WHEN EXISTS (SELECT 1 FROM reservas r WHERE r.id_pasajero_reserva = u.id_usuario) THEN 1 ELSE 0 END) as pasajeros " +
                "FROM usuarios u " +
                "INNER JOIN roles r ON u.id_rol = r.id_rol " +
                "WHERE u.fecha_baja IS NULL " +
                "GROUP BY r.nombre";

        Map<String, Integer> estadisticas = new HashMap<>();
        int totalConductores = 0;
        int totalPasajeros = 0;
        int totalAdmins = 0;
        int totalUsuarios = 0;
        int totalUsuariosBase = 0;

        try (
                Connection conn = ConnectionDB.getInstancia().getConn();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()
        ) {
            while (rs.next()) {
                String rol = rs.getString("rol_base");
                int totalRol = rs.getInt("total_por_rol");
                int conductores = rs.getInt("conductores");
                int pasajeros = rs.getInt("pasajeros");

                if ("admin".equalsIgnoreCase(rol)) {
                    totalAdmins = totalRol;
                } else if ("usuario".equalsIgnoreCase(rol)) {
                    totalConductores = conductores;
                    totalPasajeros = pasajeros;
                    totalUsuariosBase = totalRol;
                }


                logger.debug("Rol: {}, Total: {}, Conductores: {}, Pasajeros: {}",
                        rol, totalRol, conductores, pasajeros);
            }
            totalUsuarios = totalAdmins + totalConductores + totalPasajeros;

            estadisticas.put("totalConductores", totalConductores);
            estadisticas.put("totalPasajeros", totalPasajeros);
            estadisticas.put("totalAdmins", totalAdmins);
            estadisticas.put("totalUsuarios", totalUsuarios);
            estadisticas.put("totalUsuariosBase", totalUsuariosBase);

            logger.info("Estadísticas detalladas obtenidas: Conductores={}, Pasajeros={}, Admins={}",
                    totalConductores, totalPasajeros, totalAdmins);
            return estadisticas;

        } catch (SQLException e) {
            logger.error("Error al obtener estadísticas detalladas de usuarios - Estado: {} - Código: {}",
                    e.getSQLState(), e.getErrorCode(), e);
            throw new DataAccessException("Error al obtener estadísticas de usuarios", e);
        }
    }

    public LinkedList<Usuario> obtenerUsuariosRecientes(int limite) {
        logger.debug("Obteniendo {} usuarios más recientes", limite);

        String query = "SELECT u.id_usuario, u.usuario, u.nombre, u.apellido, u.correo, u.telefono, u.id_rol, " +
                "r.nombre as nombre_rol " +
                "FROM usuarios u " +
                "INNER JOIN roles r ON u.id_rol = r.id_rol " +
                "WHERE u.fecha_baja IS NULL " +
                "ORDER BY u.id_usuario DESC LIMIT ?";

        LinkedList<Usuario> usuarios = new LinkedList<>();

        try (
                Connection conn = ConnectionDB.getInstancia().getConn();
                PreparedStatement stmt = conn.prepareStatement(query)
        ) {
            stmt.setInt(1, limite);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    usuarios.add(mapUsuarioWithRol(rs));
                }
            }

            logger.info("Obtenidos {} usuarios recientes", usuarios.size());
            return usuarios;

        } catch (SQLException e) {
            logger.error("Error al obtener usuarios recientes - Estado: {} - Código: {}",
                    e.getSQLState(), e.getErrorCode(), e);
            throw new DataAccessException("Error al obtener usuarios recientes", e);
        }
    }


    private Usuario mapUsuario(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setIdUsuario(rs.getInt("id_usuario"));
        u.setUsuario(rs.getString("usuario"));
        u.setNombre(rs.getString("nombre"));
        u.setApellido(rs.getString("apellido"));
        u.setCorreo(rs.getString("correo"));
        u.setTelefono(rs.getString("telefono"));
        Rol rol = new Rol();
        rol.setIdRol(rs.getInt("id_rol"));
        u.setRol(rol);
        return u;
    }

    private Usuario mapUsuarioWithRol(ResultSet rs) throws SQLException {
        Usuario u = mapUsuario(rs);
        u.getRol().setNombreRol(rs.getString("nombre_rol"));
        return u;
    }
}