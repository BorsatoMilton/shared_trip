package servlet;

import java.io.IOException;
import java.util.LinkedList;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.InputValidator;

@WebServlet("/usuarios")
public class CRUDusuarioAdmin extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final UserController usuarioCtrl = new UserController();
    private final RolController rolCtrl = new RolController();
    private final InputValidator inputValidator = new InputValidator();
    private static final Logger logger = LoggerFactory.getLogger(CRUDusuarioAdmin.class);


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

        if (usuario.getRol() != 1) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        try {

            Usuario usuarioActualizado = usuarioCtrl.getOneById(usuario.getIdUsuario());

            if (usuarioActualizado != null) {

                if (usuarioActualizado.getNombreRol() == null) {
                    LinkedList<Rol> roles = rolCtrl.getAll();
                    for (Rol r : roles) {
                        if (usuarioActualizado.getRol() == r.getIdRol()) {
                            usuarioActualizado.setNombreRol(r.getNombre());
                            break;
                        }
                    }
                }

                session.setAttribute("usuario", usuarioActualizado);
            }

            LinkedList<Usuario> usuarios = usuarioCtrl.getAll();
            LinkedList<Rol> roles = rolCtrl.getAll();

            for (Usuario u : usuarios) {
                for (Rol r : roles) {
                    if (u.getRol() == r.getIdRol()) {
                        u.setNombreRol(r.getNombre());
                    }
                }
            }

            request.setAttribute("usuarios", usuarios);
            request.setAttribute("roles", roles);
            request.getRequestDispatcher("usuarios.jsp").forward(request, response);

        } catch (Exception e) {
            logger.error("Error al obtener los usuarios: {}", e.getMessage());
            session.setAttribute("error", "Error cargando usuarios.");
            response.sendRedirect(request.getContextPath() + "/");
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        String action = request.getParameter("action");
        Usuario logueado = (Usuario) session.getAttribute("usuario");
        String rol = (logueado == null ? null : session.getAttribute("rol").toString());
        String redirectPage = "/";

        try {
            if ("update".equals(action)) {
                actualizarUsuario(request, logueado);
                session.setAttribute("mensaje", "Usuario actualizado con éxito");
                redirectPage = "/usuarios";
            } else if ("delete".equals(action)) {
                eliminarUsuario(request, logueado);
                session.setAttribute("mensaje", "Usuario eliminado con éxito");
                redirectPage = "/usuarios";
            } else if ("add".equals(action)) {
                crearUsuario(request, logueado);
                session.setAttribute("mensaje", "Usuario creado con éxito");
                redirectPage = "/usuarios";
            } else if ("register".equals(action)) {
                registrarUsuario(request);
                limpiarDatosFormulario(session);
                session.setAttribute("mensaje", "Usuario registrado con éxito");
                redirectPage = "/login.jsp";
            }

        } catch (Exception e) {

                preservarDatosFormulario(request, session);
                String error = e.getMessage();

                switch (error) {

                    case "USER_AND_EMAIL_EXISTS":
                        session.setAttribute("error", "El usuario y el correo ya están registrados.");
                        session.removeAttribute("usuarioFormRegister");
                        session.removeAttribute("correo");
                        break;

                    case "USERNAME_EXISTS":
                        session.setAttribute("error", "El nombre de usuario ya está en uso.");
                        session.removeAttribute("usuarioFormRegister");
                        break;

                    case "EMAIL_EXISTS":
                        session.setAttribute("error", "El correo electrónico ya está registrado.");
                        session.removeAttribute("correo");
                        break;

                    default:
                        session.setAttribute("error", e.getMessage());
                }

                if ("register".equals(action)) {
                    redirectPage = "/register.jsp";
                }else if (logueado == null) {
                    redirectPage = "/login.jsp";
                }else if ("admin".equals(rol)) {
                    redirectPage = "/usuarios.jsp";
                }else {
                    redirectPage = "/";
                }

        }
        response.sendRedirect(request.getContextPath() + redirectPage);
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
        } else if (logueado.getRol() == 1 && logueado.getIdUsuario() == id) {
            rol = inputValidator.validarRol(request.getParameter("rol"));
        } else if (logueado.getIdUsuario() == id) {
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


    private void preservarDatosFormulario(HttpServletRequest request, HttpSession session) {
        session.setAttribute("nombre", request.getParameter("nombre"));
        session.setAttribute("apellido", request.getParameter("apellido"));
        session.setAttribute("correo", request.getParameter("correo"));
        session.setAttribute("usuarioFormRegister", request.getParameter("usuario"));
        session.setAttribute("telefono", request.getParameter("telefono"));
    }


    private void limpiarDatosFormulario(HttpSession session) {
        session.removeAttribute("nombre");
        session.removeAttribute("apellido");
        session.removeAttribute("correo");
        session.removeAttribute("usuarioFormRegister");
        session.removeAttribute("telefono");
    }


}