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
import validators.InputValidator;

@WebServlet("/usuarios")
public class CRUDusuarioAdmin extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private UserController usuarioCtrl = new UserController();
	private RolController rolCtrl = new RolController();
    private InputValidator inputValidator = new InputValidator();


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

        String usuario = inputValidator.validarUsuario(request.getParameter("usuario"));
        String clave = inputValidator.validarClave(request.getParameter("clave"));
        String nombre = inputValidator.validarNombre(request.getParameter("nombre"));
        String apellido = inputValidator.validarApellido(request.getParameter("apellido"));
        String correo = inputValidator.validarEmail(request.getParameter("correo"));
        String telefono = inputValidator.validarTelefono(request.getParameter("telefono"));
        int rol = inputValidator.validarRol(request.getParameter("rol"));

        usuarioCtrl.crearUsuario(usuario, clave, nombre, apellido, correo, telefono, rol);
    }

    private void registrarUsuario(HttpServletRequest request) throws Exception {

        String usuario = inputValidator.validarUsuario(request.getParameter("usuario"));
        String clave = inputValidator.validarClave(request.getParameter("clave"));
        String nombre = inputValidator.validarNombre(request.getParameter("nombre"));
        String apellido = inputValidator.validarApellido(request.getParameter("apellido"));
        String correo = inputValidator.validarEmail(request.getParameter("correo"));
        String telefono = inputValidator.validarTelefono(request.getParameter("telefono"));

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

        String usuario = inputValidator.validarUsuario(request.getParameter("usuario"));
        String nombre = inputValidator.validarNombre(request.getParameter("nombre"));
        String apellido = inputValidator.validarApellido(request.getParameter("apellido"));
        String correo = inputValidator.validarEmail(request.getParameter("correo"));
        String telefono = inputValidator.validarTelefono(request.getParameter("telefono"));


        String clave = request.getParameter("clave");
        if (clave != null && !clave.trim().isEmpty()) {
            clave = inputValidator.validarClave(clave);
        } else {
            clave = null;
        }

        Integer rol = null;

        if (logueado.getRol() == 1 && logueado.getIdUsuario() != id) {
            rol = inputValidator.validarRol(request.getParameter("rol"));
        }

        else if (logueado.getRol() == 1 && logueado.getIdUsuario() == id) {
            rol = inputValidator.validarRol(request.getParameter("rol"));
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


}