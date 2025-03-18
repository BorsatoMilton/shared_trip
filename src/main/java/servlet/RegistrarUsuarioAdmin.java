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

/**
 * Servlet implementation class RegistrarUsuarioAdmin
 */
@WebServlet("/usuarios")
public class RegistrarUsuarioAdmin extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RegistrarUsuarioAdmin() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.getSession().removeAttribute("roles");
		request.getSession().removeAttribute("usuarios");
		
		UserController usuarioCtrl = new UserController();
		RolController rolCtrl = new RolController();
		if ("admin".equals(request.getSession().getAttribute("rol"))) {
			LinkedList<Rol> roles = rolCtrl.getAll();
			LinkedList<Usuario> usuarios = usuarioCtrl.getAll();
			request.getSession().setAttribute("roles", roles);
			request.getSession().setAttribute("usuarios", usuarios);
			request.getRequestDispatcher("usuarios.jsp").forward(request, response);
		}else {
			request.getSession().setAttribute("mensaje", "Acceso denegado");
			response.sendRedirect(request.getContextPath() +"/");
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(true);
		UserController ctrl = new UserController();
		Usuario usuario = ctrl.getOneByUserOrEmail(request.getParameter("usuario"), request.getParameter("correo"));
		
		if(usuario.getUsuario() != null) {
			session.setAttribute("mensaje", "Ya existe un usuario registrado con ese usuario o correo");
		}else {
			Usuario u = new Usuario();

			u.setNombre(request.getParameter("nombre"));
			u.setApellido(request.getParameter("apellido"));
			u.setCorreo(request.getParameter("correo"));
			u.setUsuario(request.getParameter("usuario"));
			u.setClave(request.getParameter("clave"));
			u.setTelefono(request.getParameter("telefono"));
			u.setRol(Integer.parseInt(request.getParameter("rol")));
			
			ctrl.addUser(u);
			
			session.setAttribute("mensaje", "Usuario creado con éxito");
		}
				
		
		response.sendRedirect(request.getContextPath() + "/usuarios");
	}

}
