package servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import entidades.Usuario;
import logic.UserController;


@WebServlet("/perfil")
public class Perfil extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private UserController usuarioCtrl = new UserController();
       

    public Perfil() {
        super();
    }


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		if(request.getSession().getAttribute("usuario") == null) {
			response.sendRedirect(request.getContextPath() + "/login.jsp");
			return;
		};
		Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		int idUsuario = usuario.getIdUsuario();
		request.getSession().removeAttribute("usuario");
		Usuario usuarioNuevo = usuarioCtrl.getOneById(idUsuario);
		request.getSession().setAttribute("usuario", usuarioNuevo);
		
		response.sendRedirect(request.getContextPath() + "/perfil.jsp");
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			
		
		HttpSession session = request.getSession();
		String action = request.getParameter("action");
		try {
			if ("profile".equals(action)) {
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
				
			} else {
				throw new Exception("No se especifico la acción");
			}
			
		}catch(Exception e) {
			session.setAttribute("error", "Error: " + e.getMessage());
			System.out.println("Error en editarUsuario: " + e.getMessage());
		}
		
		response.sendRedirect(request.getContextPath() + "/perfil");
	}
	
	
	private boolean actualizarUsuario(HttpServletRequest request) throws Exception {
		int id = Integer.parseInt(request.getParameter("idUsuario"));
		Usuario u = usuarioCtrl.getOneById(id);

		if (u == null) {
			throw new Exception("Usuario no encontrado");
		}

		cargarDatosUsuario(request, u);
		return usuarioCtrl.updateUser(u, id);
	}
	
	
	private boolean actualizarClave(HttpServletRequest request) throws Exception {
		int id = Integer.parseInt(request.getParameter("idUsuario"));
		Usuario u = usuarioCtrl.getOneById(id);
		if (u == null) {
			throw new Exception("Usuario no encontrado");
		}

	   return usuarioCtrl.updatePassword(id, request.getParameter("clave"));
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
