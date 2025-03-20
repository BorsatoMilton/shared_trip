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

/**
 * Servlet implementation class Register
 */
@WebServlet("/Register")
public class Register extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Register() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
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
			u.setRol(2); //BUSCAR EL ROL Y ACOMODARLO
			
			ctrl.addUser(u);
			
			session.setAttribute("mensaje", "Usuario creado con éxito");

		}

		response.sendRedirect(request.getContextPath() + "/register.jsp");

	}

}
