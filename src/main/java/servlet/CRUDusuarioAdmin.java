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

@WebServlet("/usuarios")
public class CRUDusuarioAdmin extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private UserController usuarioCtrl = new UserController();
	private RolController rolCtrl = new RolController();

	public CRUDusuarioAdmin() {
		super();
		// TODO Auto-generated constructor stub
	}


    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
        if (usuario == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        Usuario usuarioActualizado = usuarioCtrl.getOneById(usuario.getIdUsuario());
        request.getSession().setAttribute("usuario", usuarioActualizado);

        if (usuarioActualizado.getRol() == 1) {
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
            return;
        }

        request.setAttribute("usuario", usuarioActualizado);
        request.getRequestDispatcher("perfil.jsp").forward(request, response);
    }


    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        String action = request.getParameter("action");
        Usuario logueado = (Usuario) session.getAttribute("usuario");
        String rol = (logueado == null ? null : session.getAttribute("rol").toString());


        try {
            if ("update".equals(action)) {
                if(actualizarUsuario(request)) {
                    session.setAttribute("mensaje", "Usuario actualizado con éxito");
                }else {
                    session.setAttribute("mensaje", "Ocurrio un error al actualizar el usuario");
                }
            } else if ("password".equals(action)) {
                if(actualizarClave(request)) {
                    session.setAttribute("mensaje", "Clave actualizada con éxito");
                }else {
                    session.setAttribute("mensaje", "Ocurrio un error al actualizar la clave");
                }
            } else if ("delete".equals(action)) {
                if(eliminarUsuario(request)) {
                    session.setAttribute("mensaje", "Usuario eliminado con éxito");
                }else {
                    session.setAttribute("mensaje", "Ocurrio un error al eliminar el usuario");
                }
            } else if ("add".equals(action)) {
                if(crearUsuario(request)) {
                    session.setAttribute("mensaje", "Usuario creado con éxito");
                }else {
                    session.setAttribute("mensaje", "Ocurrio un error al crear el usuario");
                }
            } else if ("register".equals(action)) {
                if(crearUsuario(request)) {
                    session.setAttribute("mensaje", "Usuario creado con éxito");
                }else {
                    session.setAttribute("mensaje", "Ocurrio un error al crear el usuario");
                }
            }

        } catch (Exception e) {
            session.setAttribute("error", "Error: " + e.getMessage());
            System.out.println("Error en operación: " + e.getMessage());
        }

        if ("register".equals(action)) {
            response.sendRedirect(request.getContextPath() + "/register.jsp");
            return;
        }

        if (logueado == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        if ("admin".equals(rol)) {
            response.sendRedirect(request.getContextPath() + "/usuarios");
            return;
        }

        if ("usuario".equals(rol)) {
            response.sendRedirect(request.getContextPath() + "/perfil.jsp");
            return;
        }

    }

    private boolean crearUsuario(HttpServletRequest request) throws Exception {
        Usuario u = new Usuario();
        cargarDatosUsuario(request, u);

        if (usuarioCtrl.getOneByUserOrEmail(u.getUsuario(), u.getCorreo()) != null) {
            throw new Exception("Usuario o correo ya registrado");
        }

        return usuarioCtrl.addUser(u);
    }

    private boolean actualizarUsuario(HttpServletRequest request) throws Exception {
        int id = 0;

        if(request.getParameter("from") != null && "profile".equals(request.getParameter("from"))){
            id = ((Usuario) request.getSession().getAttribute("usuario")).getIdUsuario();
        }else {
            id = Integer.parseInt(request.getParameter("idUsuario"));
        }

        Usuario u = usuarioCtrl.getOneById(id);

        if (u == null) {
            throw new Exception("Usuario no encontrado");
        }

        cargarDatosUsuario(request, u);
        return usuarioCtrl.updateUser(u);
    }

    private boolean actualizarClave(HttpServletRequest request) throws Exception {
        Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
        if (usuario == null) {
            throw new Exception("Usuario no encontrado");
        }
        int id = usuario.getIdUsuario();
        return usuarioCtrl.updatePassword(id, request.getParameter("clave"));
    }

    private boolean eliminarUsuario(HttpServletRequest request) throws Exception {
        int id = Integer.parseInt(request.getParameter("idUsuario"));
        Usuario u = usuarioCtrl.getOneById(id);

        if (u == null) {
            throw new Exception("Usuario no encontrado");
        }

        return usuarioCtrl.deleteUser(id);
    }

    private void cargarDatosUsuario(HttpServletRequest request, Usuario u) {
        u.setNombre(request.getParameter("nombre"));
        u.setApellido(request.getParameter("apellido"));
        u.setCorreo(request.getParameter("correo"));
        u.setUsuario(request.getParameter("usuario"));
        u.setClave(request.getParameter("clave"));
        u.setTelefono(request.getParameter("telefono"));

        if ("add".equals(request.getParameter("action"))) {
            Usuario enSesion = (Usuario) request.getSession().getAttribute("usuario");
            if (enSesion != null && enSesion.getRol() == 1) {  // rol 1 = admin
                u.setRol(Integer.parseInt(request.getParameter("rol")));
            }
        }else if (("register").equals(request.getParameter("action"))) {
            u.setRol(2); // CAMBIARLO
        }else if(("update").equals(request.getParameter("action"))){
            if(request.getParameter("from") != null && "profile".equals(request.getParameter("from"))){
                Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
                int rol = usuario.getRol();
                u.setRol(rol);
            }else {
                u.setRol(Integer.parseInt(request.getParameter("rol")));
            }
        }
    }
}