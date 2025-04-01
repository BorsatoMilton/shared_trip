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

		if (!"admin".equals(request.getSession().getAttribute("rol"))) {
			request.getSession().setAttribute("mensaje", "Acceso denegado");
			response.sendRedirect(request.getContextPath() + "/");
			return;
		}
		request.removeAttribute("roles");
		request.removeAttribute("usuarios");

		LinkedList<Rol> roles = rolCtrl.getAll();
		LinkedList<Usuario> usuarios = usuarioCtrl.getAll();
		for (Usuario u : usuarios) {
			Rol rol = rolCtrl.getOne(u.getRol());
			u.setNombreRol(rol.getNombre());
		}

		request.setAttribute("roles", roles);
		request.setAttribute("usuarios", usuarios);
		request.getRequestDispatcher("usuarios.jsp").forward(request, response);
		;

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		String action = request.getParameter("action");
		

		try {
			if ("update".equals(action)) {
				if(actualizarUsuario(request)) {
					session.setAttribute("mensaje", "Usuario actualizado con éxito");
				}else {
					session.setAttribute("mensaje", "Ocurrio un error al actualizar el usuario");
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
			}

		} catch (Exception e) {
			session.setAttribute("error", "Error: " + e.getMessage());
			System.out.println("Error en crearUsuario: " + e.getMessage());
		}

		response.sendRedirect(request.getContextPath() + "/usuarios");
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
		int id = Integer.parseInt(request.getParameter("idUsuario"));
		Usuario u = usuarioCtrl.getOneById(id);

		if (u == null) {
			throw new Exception("Usuario no encontrado");
		}

		cargarDatosUsuario(request, u);
		return usuarioCtrl.updateUser(u);
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
		u.setRol(Integer.parseInt(request.getParameter("rol")));

	}

}
