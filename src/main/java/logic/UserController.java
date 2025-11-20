package logic;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;

import data.*;
import entidades.*;

public class UserController {
    private final UserDAO usuarioDAO;

    public UserController() {
        this.usuarioDAO = new UserDAO();
    }

    public Usuario login(Usuario u) {

        String claveEncriptada = encriptarClave(u.getClave());
        u.setClave(claveEncriptada);
        return usuarioDAO.login(u);
    }


    public Usuario getOneById(int id) {
        return usuarioDAO.getById(id);
    }

    public LinkedList<Usuario> getAll() {
        return usuarioDAO.getAll();
    }

    public void actualizarUsuario(int id, String usuario, String clave, String nombre,
                                  String apellido, String correo, String telefono,
                                  Integer rol, Usuario logueado) throws Exception {


        Usuario u = usuarioDAO.getById(id);
        if (u == null) {
            throw new Exception("El usuario no existe");
        }

        boolean esAdmin = (logueado.getRol() == 1);
        boolean esElMismo = (logueado.getIdUsuario() == id);

        if (!esAdmin && !esElMismo) {
            throw new Exception("No tiene permisos para modificar este usuario");
        }

        if (rol != null) {
            if (!esAdmin) {
                throw new Exception("No tiene permisos para cambiar roles");
            }

            if (u.getRol() == 1 && rol != 1) {
                int cantidadAdmins = usuarioDAO.contarAdmins();
                if (cantidadAdmins <= 1) {
                    throw new Exception("No se puede quitar el rol admin al último administrador");
                }
            }

            u.setRol(rol);
        }


        if (!u.getUsuario().equals(usuario) || !u.getCorreo().equals(correo)) {
            Usuario existente = usuarioDAO.getOneByUserOrEmail(usuario, correo, id);
            if (existente != null && existente.getIdUsuario() != id) {
                if (existente.getUsuario().equalsIgnoreCase(usuario)) {
                    throw new Exception("El nombre de usuario ya está en uso por otro usuario");
                } else {
                    throw new Exception("El correo ya está registrado por otro usuario");
                }
            }
        }

        u.setUsuario(usuario);
        u.setNombre(nombre);
        u.setApellido(apellido);
        u.setCorreo(correo);
        u.setTelefono(telefono);


        if (clave != null && !clave.isEmpty()) {
            String claveEncriptada = encriptarClave(clave);
            u.setClave(claveEncriptada);
        }

        boolean actualizado = usuarioDAO.update(u);
        if (!actualizado) {
            throw new Exception("Error al actualizar el usuario en la base de datos");
        }
    }

    public boolean updatePassword(int id, String clave) {
        return usuarioDAO.updatePassword(id, clave);
    }

    public void crearUsuario(String usuario, String clave, String nombre,
                             String apellido, String correo, String telefono,
                             int rol) throws Exception {

        Usuario existente = usuarioDAO.getOneByUserOrEmail(usuario, correo, null);
        if (existente != null) {
            if (existente.getUsuario().equalsIgnoreCase(usuario) &&
                    existente.getCorreo().equalsIgnoreCase(correo)) {
                throw new Exception("El usuario y el correo ya están registrados");
            } else if (existente.getUsuario().equalsIgnoreCase(usuario)) {
                throw new Exception("El nombre de usuario ya está en uso");
            } else {
                throw new Exception("El correo electrónico ya está registrado");
            }
        }

        String claveEncriptada = encriptarClave(clave);


        Usuario u = new Usuario();
        u.setUsuario(usuario);
        u.setClave(claveEncriptada);
        u.setNombre(nombre);
        u.setApellido(apellido);
        u.setCorreo(correo);
        u.setTelefono(telefono);
        u.setRol(rol);

        boolean creado = usuarioDAO.add(u);
        if (!creado) {
            throw new Exception("Error al guardar el usuario en la base de datos");
        }
    }

    public void eliminarUsuario(int id, Usuario logueado) throws Exception {


        Usuario u = usuarioDAO.getById(id);
        if (u == null) {
            throw new Exception("El usuario no existe");
        }

        if (logueado.getRol() != 1) {
            throw new Exception("No tiene permisos para eliminar usuarios");
        }

        if (logueado.getIdUsuario() == id) {
            throw new Exception("No puede eliminarse a sí mismo");
        }

        if (u.getRol() == 1) {
            int cantidadAdmins = usuarioDAO.contarAdmins();
            if (cantidadAdmins <= 1) {
                throw new Exception("No se puede eliminar al último administrador del sistema");
            }
        }

        if (usuarioDAO.tieneViajesActivos(id)) {
            throw new Exception("No se puede eliminar un usuario con viajes activos. " +
                    "Cancele o complete los viajes primero.");
        }

        if (usuarioDAO.tieneReservasActivas(id)) {
            throw new Exception("No se puede eliminar un usuario con reservas activas. " +
                    "Cancele las reservas primero.");
        }

        boolean eliminado = usuarioDAO.eliminarUsuario(id);
        if (!eliminado) {
            throw new Exception("Error al eliminar el usuario de la base de datos");
        }
    }


    private String encriptarClave(String clave) {
        return clave;
        /*try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(clave.getBytes());
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al encriptar la contraseña", e);
        }*/
    }

}