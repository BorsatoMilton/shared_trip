package servlet;

import java.io.IOException;
import java.util.LinkedList;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import entidades.Rol;
import entidades.Usuario;
import logic.RolController;
import logic.UserController;

@WebServlet("/usuarios")
public class CRUDusuarioAdmin extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private UserController usuarioCtrl = new UserController();
	private RolController rolCtrl = new RolController();

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    private static final Pattern TELEFONO_PATTERN = Pattern.compile(
            "^\\+?[0-9]{8,15}$"
    );
    private static final Pattern USUARIO_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._-]{3,20}$"
    );

	public CRUDusuarioAdmin() {
		super();
		// TODO Auto-generated constructor stub
	}


    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("usuario") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        Usuario usuarioActualizado = usuarioCtrl.getOneById(usuario.getIdUsuario());
        request.getSession().setAttribute("usuario", usuarioActualizado);

        if(usuarioActualizado.getRol() == 1) {
            LinkedList<Usuario> usuarios = usuarioCtrl.getAll();
            LinkedList<Rol> roles = rolCtrl.getAll();

            for (Usuario u : usuarios) {
                for (Rol r : roles) {
                    if(u.getRol() == r.getIdRol()) {
                        u.setNombreRol(r.getNombre());
                    }
                }
            }

            request.setAttribute("usuarios", usuarios);
            request.setAttribute("roles", roles);
            request.getRequestDispatcher("usuarios.jsp").forward(request, response);
        }else {
            response.sendRedirect(request.getContextPath() + "/");
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        String action = request.getParameter("action");
        Usuario logueado = (Usuario) session.getAttribute("usuario");
        String rol = (logueado == null ? null : session.getAttribute("rol").toString());


        try {
            if ("update".equals(action)) {
                actualizarUsuario(request, logueado);
                session.setAttribute("mensaje", "Usuario actualizado con éxito");

            } else if ("delete".equals(action)) {
                eliminarUsuario(request, logueado);
                session.setAttribute("mensaje", "Usuario eliminado con éxito");

            } else if ("add".equals(action)) {
                crearUsuario(request, logueado);
                session.setAttribute("mensaje", "Usuario creado con éxito");

            } else if ("register".equals(action)) {
                registrarUsuario(request);
                session.setAttribute("mensaje", "Usuario registrado con éxito");
            }

        } catch (Exception e) {
            session.setAttribute("error", e.getMessage());
            System.out.println("Error en operación: " + e.getMessage());
        }

        if ("register".equals(action)) {
        	preservarDatosFormulario(request, session);
            response.sendRedirect(request.getContextPath() + "/register.jsp");
            return;
        }

        if (logueado == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        if ("admin".equals(rol)) {
            response.sendRedirect(request.getContextPath() + "/usuarios");
        }

    }
    
    private void preservarDatosFormulario(HttpServletRequest request, HttpSession session) {
        session.setAttribute("formData_nombre", request.getParameter("nombre"));
        session.setAttribute("formData_apellido", request.getParameter("apellido"));
        session.setAttribute("formData_correo", request.getParameter("correo"));
        session.setAttribute("formData_usuario", request.getParameter("usuario"));
        session.setAttribute("formData_telefono", request.getParameter("telefono"));
    }

    private void crearUsuario(HttpServletRequest request, Usuario admin) throws Exception {

        if (admin == null || admin.getRol() != 1) {
            throw new Exception("No tiene permisos para crear usuarios");
        }

        String usuario = validarUsuario(request.getParameter("usuario"));
        String clave = validarClave(request.getParameter("clave"));
        String nombre = validarNombre(request.getParameter("nombre"));
        String apellido = validarApellido(request.getParameter("apellido"));
        String correo = validarEmail(request.getParameter("correo"));
        String telefono = validarTelefono(request.getParameter("telefono"));
        int rol = validarRol(request.getParameter("rol"));

        usuarioCtrl.crearUsuario(usuario, clave, nombre, apellido, correo, telefono, rol);
    }

    private void registrarUsuario(HttpServletRequest request) throws Exception {

        String usuario = validarUsuario(request.getParameter("usuario"));
        String clave = validarClave(request.getParameter("clave"));
        String nombre = validarNombre(request.getParameter("nombre"));
        String apellido = validarApellido(request.getParameter("apellido"));
        String correo = validarEmail(request.getParameter("correo"));
        String telefono = validarTelefono(request.getParameter("telefono"));

        int rol = 2; // usuario normal

        usuarioCtrl.crearUsuario(usuario, clave, nombre, apellido, correo, telefono, rol);
    }

    private void actualizarUsuario(HttpServletRequest request, Usuario logueado) throws Exception {

        String idStr = request.getParameter("idUsuario");
        if (idStr == null || idStr.trim().isEmpty()) {
            throw new Exception("ID de usuario inválido");
        }

        int id;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            throw new Exception("ID de usuario debe ser un número");
        }

        String usuario = validarUsuario(request.getParameter("usuario"));
        String nombre = validarNombre(request.getParameter("nombre"));
        String apellido = validarApellido(request.getParameter("apellido"));
        String correo = validarEmail(request.getParameter("correo"));
        String telefono = validarTelefono(request.getParameter("telefono"));


        String clave = request.getParameter("clave");
        if (clave != null && !clave.trim().isEmpty()) {
            clave = validarClave(clave);
        } else {
            clave = null;
        }

        Integer rol = null;

        if (logueado.getRol() == 1 && logueado.getIdUsuario() != id) {
            rol = validarRol(request.getParameter("rol"));
        }

        else if (logueado.getRol() == 1 && logueado.getIdUsuario() == id) {
            rol = validarRol(request.getParameter("rol"));
        }

        else if (logueado.getIdUsuario() == id) {
            rol = null; // No actualizar rol
        }

        usuarioCtrl.actualizarUsuario(id, usuario, clave, nombre, apellido,
                correo, telefono, rol, logueado);
    }
    private void eliminarUsuario(HttpServletRequest request, Usuario logueado) throws Exception {

        String idStr = request.getParameter("idUsuario");
        if (idStr == null || idStr.trim().isEmpty()) {
            throw new Exception("ID de usuario inválido");
        }

        int id;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            throw new Exception("ID de usuario debe ser un número");
        }

        usuarioCtrl.eliminarUsuario(id, logueado);
    }

    private String validarUsuario(String usuario) throws Exception {
        if (usuario == null || usuario.trim().isEmpty()) {
            throw new Exception("El nombre de usuario es obligatorio");
        }

        usuario = usuario.trim();

        if (!USUARIO_PATTERN.matcher(usuario).matches()) {
            throw new Exception("El usuario debe tener entre 3-20 caracteres " +
                    "(solo letras, números, puntos, guiones)");
        }

        return usuario;
    }

    private String validarClave(String clave) throws Exception {
        if (clave == null || clave.trim().isEmpty()) {
            throw new Exception("La contraseña es obligatoria");
        }

        if (clave.length() < 6) {
            throw new Exception("La contraseña debe tener al menos 6 caracteres");
        }

        if (clave.length() > 100) {
            throw new Exception("La contraseña es demasiado larga");
        }

        if (!clave.matches(".*[A-Za-z].*") || !clave.matches(".*[0-9].*")) {
            throw new Exception("La contraseña debe contener letras y números");
        }

        return clave;
    }

    private String validarNombre(String nombre) throws Exception {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new Exception("El nombre es obligatorio");
        }

        nombre = nombre.trim();

        if (nombre.length() < 2 || nombre.length() > 50) {
            throw new Exception("El nombre debe tener entre 2 y 50 caracteres");
        }

        if (!nombre.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")) {
            throw new Exception("El nombre solo puede contener letras");
        }

        return nombre;
    }

    private String validarApellido(String apellido) throws Exception {
        if (apellido == null || apellido.trim().isEmpty()) {
            throw new Exception("El apellido es obligatorio");
        }

        apellido = apellido.trim();

        if (apellido.length() < 2 || apellido.length() > 50) {
            throw new Exception("El apellido debe tener entre 2 y 50 caracteres");
        }

        if (!apellido.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")) {
            throw new Exception("El apellido solo puede contener letras");
        }

        return apellido;
    }

    private String validarEmail(String email) throws Exception {
        if (email == null || email.trim().isEmpty()) {
            throw new Exception("El email es obligatorio");
        }

        email = email.trim().toLowerCase();

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new Exception("Formato de email inválido");
        }

        if (email.length() > 100) {
            throw new Exception("El email es demasiado largo");
        }

        return email;
    }

    private String validarTelefono(String telefono) throws Exception {
        if (telefono == null || telefono.trim().isEmpty()) {
            throw new Exception("El teléfono es obligatorio");
        }

        telefono = telefono.trim().replaceAll("[\\s-]", "");

        if (!TELEFONO_PATTERN.matcher(telefono).matches()) {
            throw new Exception("Formato de teléfono inválido (8-15 dígitos)");
        }

        return telefono;
    }

    private int validarRol(String rolStr) throws Exception {
        if (rolStr == null || rolStr.trim().isEmpty()) {
            throw new Exception("El rol es obligatorio");
        }

        int rol;
        try {
            rol = Integer.parseInt(rolStr);
        } catch (NumberFormatException e) {
            throw new Exception("El rol debe ser un número");
        }

        if (rol < 1 || rol > 2) {
            throw new Exception("Rol inválido");
        }

        return rol;
    }
}