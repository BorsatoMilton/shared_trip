package data;

import entidades.*;

import java.sql.*;
import java.util.LinkedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class UserDAO {

	private static final Logger logger = LoggerFactory.getLogger(UserDAO.class);


	public LinkedList<Usuario> getAll() {
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
	    } catch (SQLException e) {
	         logger.error("Error al obtener los usuarios", e);

	    } finally {
	         ConnectionDB.getInstancia().releaseConn();
	    }
	    return users;
	}

	public Usuario login(Usuario user) {

		    String query = "SELECT id_usuario, usuario, usuarios.nombre AS nombre, apellido, correo, telefono, id_rol, roles.nombre AS nombre_rol " +
		                   "FROM usuarios INNER JOIN roles ON roles.id = usuarios.id_rol " +
		                   "WHERE usuario = ? AND clave = ? AND fecha_baja IS NULL";
		    Connection conn = null;
		    try {
		        conn = ConnectionDB.getInstancia().getConn();

		        try (PreparedStatement stmt = conn.prepareStatement(query)) {
		            stmt.setString(1, user.getUsuario());
		            stmt.setString(2, user.getClave());
		            try (ResultSet rs = stmt.executeQuery()) {
		                if (rs.next()) {
                            System.out.println("Usuario logueado!");
		                    return mapUsuarioWithRol(rs);
		                }
		            }
		        }
		    } catch (SQLException e) {
		        logger.error("Error al realizar login", e);
		    } finally {
		        ConnectionDB.getInstancia().releaseConn();
		    }
		    return null;
		}


		public Usuario getById(int id_usuario) {

			String query = "SELECT id_usuario, usuario, nombre, apellido, correo, telefono, id_rol "
					+ "FROM usuarios WHERE id_usuario = ? AND fecha_baja IS NULL";

			Connection conn = null;

			try {

				conn = ConnectionDB.getInstancia().getConn();

				try (PreparedStatement stmt = conn.prepareStatement(query)) {
					stmt.setInt(1, id_usuario);

					try (ResultSet rs = stmt.executeQuery()) {
						if (rs.next()) {
							return mapUsuario(rs);
						}
					}
				}
			} catch (SQLException e) {
				logger.error("Error al obtener el usuario", e);
			} finally {
				ConnectionDB.getInstancia().releaseConn();
			}

			return null;
		}

		public Usuario getOneUserByEmail(String correo) {
		    String query = "SELECT id_usuario, usuario, nombre, apellido, correo, telefono, id_rol "
		                 + "FROM usuarios WHERE correo = ? AND fecha_baja IS NULL";

		    try (Connection conn = ConnectionDB.getInstancia().getConn();
		         PreparedStatement stmt = conn.prepareStatement(query)) {

		        stmt.setString(1, correo);

		        try (ResultSet rs = stmt.executeQuery()) {
		            if (rs.next()) {
		                return mapUsuario(rs);
		            }
		        }
		    } catch (SQLException e) {
		        logger.error("Error al obtener el usuario por nombre de usuario o correo", e);
		    } finally {
		        ConnectionDB.getInstancia().releaseConn();
		    }

		    return null;
		}

		
		public Usuario getOneByUserOrEmail(String user, String correo) {
		    String query = "SELECT id_usuario, usuario, nombre, apellido, correo, telefono, id_rol "
		                 + "FROM usuarios WHERE (usuario = ? OR correo = ?) AND fecha_baja IS NULL";

		    try (Connection conn = ConnectionDB.getInstancia().getConn();
		         PreparedStatement stmt = conn.prepareStatement(query)) {

		        stmt.setString(1, user);
		        stmt.setString(2, correo);

		        try (ResultSet rs = stmt.executeQuery()) {
		            if (rs.next()) {
		                return mapUsuario(rs);
		            }
		        }
		    } catch (SQLException e) {
		        logger.error("Error al obtener el usuario por nombre de usuario o correo", e);
		    } finally {
		        ConnectionDB.getInstancia().releaseConn();
		    }

		    return null;
		}


		public boolean add(Usuario u) {
		    String query = "INSERT INTO usuarios(usuario, clave, nombre, apellido, correo, telefono, id_rol) "
		                 + "VALUES(?,?,?,?,?,?,?)";

		    try (Connection conn = ConnectionDB.getInstancia().getConn();
		         PreparedStatement stmt = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {

		        stmt.setString(1, u.getUsuario());
		        stmt.setString(2, u.getClave());
		        stmt.setString(3, u.getNombre());
		        stmt.setString(4, u.getApellido());
		        stmt.setString(5, u.getCorreo());
		        stmt.setString(6, u.getTelefono());
		        stmt.setInt(7, u.getRol());

		        stmt.executeUpdate();

		        try (ResultSet keyResultSet = stmt.getGeneratedKeys()) {
		            if (keyResultSet.next()) {
		                u.setIdUsuario(keyResultSet.getInt(1)); //SIRVE DE ALGO? CREO QUE LUEGO NO NECESITO EL ID DEL USUARIO
		                return true;                          
		            }
		            return false;
		        }
		    } catch (SQLException e) {
		        logger.error("Error al agregar usuario", e);
		        return false;
		    } finally {
		        ConnectionDB.getInstancia().releaseConn();
		    }
		}
 
		public boolean update(Usuario u) {       
		    String query = "UPDATE usuarios SET usuario = ?, nombre = ?, apellido = ?, correo = ?, telefono = ?, id_rol = ? WHERE id_usuario = ?";

		    try (Connection conn = ConnectionDB.getInstancia().getConn();
		         PreparedStatement stmt = conn.prepareStatement(query)) {

		        stmt.setString(1, u.getUsuario());
		        stmt.setString(2, u.getNombre());
		        stmt.setString(3, u.getApellido());
		        stmt.setString(4, u.getCorreo());
		        stmt.setString(5, u.getTelefono());
		        stmt.setInt(6, u.getRol());
		        stmt.setInt(7, u.getIdUsuario());

		        int rowsAffected = stmt.executeUpdate();
		        if (rowsAffected > 0) {
		            logger.info("Usuario con ID {} actualizado correctamente.", u.getIdUsuario());
		            return true;
		        } else {
		            logger.warn("No se encontró un usuario con ID {} para actualizar.", u.getIdUsuario());
		            return false;
		        }
		        
		    } catch (SQLException e) {
		        logger.error("Error al actualizar el usuario con ID {}: {}", u.getIdUsuario(), e.getMessage(), e);
		        return false;
		    } finally {
		        ConnectionDB.getInstancia().releaseConn();
		    }
		}
	
		public boolean updatePassword(int id, String clave) {      
		    String query = "UPDATE usuarios SET clave = ? WHERE id_usuario = ?";

		    try (Connection conn = ConnectionDB.getInstancia().getConn();
		         PreparedStatement stmt = conn.prepareStatement(query)) {

		        stmt.setString(1, clave);       //falta hasheo
		        stmt.setInt(2, id);

		        int rowsAffected = stmt.executeUpdate();
		        if (rowsAffected > 0) {
		            logger.info("Contraseña del usuario con ID {} actualizada correctamente.", id);
		            return true;
		        } else {
		            logger.warn("No se encontró un usuario con ID {} para actualizar la contraseña.", id);
		            return false;
		        }

		    } catch (SQLException e) {
		        logger.error("Error al actualizar la contraseña del usuario con ID {}: {}", id, e.getMessage(), e);
		        return false;
		    } finally {
		        ConnectionDB.getInstancia().releaseConn();
		    }
		}

		public boolean eliminarUsuario(int idUsuario) {
		    String query = "UPDATE usuarios SET fecha_baja = current_date WHERE id_usuario = ?";

		    try (Connection conn = ConnectionDB.getInstancia().getConn();
		         PreparedStatement stmt = conn.prepareStatement(query)) {

		        stmt.setInt(1, idUsuario);
		        int rowsAffected = stmt.executeUpdate();

		        if (rowsAffected > 0) {
		            logger.info("Usuario con ID {} marcado como eliminado.", idUsuario);
		            return true;
		        } else {
		            logger.warn("No se encontró ningún usuario con ID {} para eliminar.", idUsuario);
		            return false;
		        }

		    } catch (SQLException e) {
		        logger.error("Error al eliminar el usuario con ID {}: {}", idUsuario, e.getMessage(), e);
		        return false;
		    } finally {
		        ConnectionDB.getInstancia().releaseConn();
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
        u.setRol(rs.getInt("id_rol"));
        return u;
    }
    
    private Usuario mapUsuarioWithRol(ResultSet rs) throws SQLException {
        Usuario u = mapUsuario(rs);
        u.setNombreRol(rs.getString("nombre_rol"));
        return u;
    }

}