package servlet;

import java.io.IOException;

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


@WebServlet({ "/signin", "/Signin", "/SIGNIN", "/SignIn", "/signIn" })
public class Signin extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public Signin() {
        super();

    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	    String usuario = request.getParameter("usuario");
	    String clave = request.getParameter("password");
	    
	    if(usuario == null || usuario.trim().isEmpty() || 
	       clave == null || clave.trim().isEmpty()) {
	        request.getSession().setAttribute("errorMessage", "Complete las credenciales");
	        response.sendRedirect(request.getContextPath() + "/login.jsp");
	        return;
	    }
		
	    HttpSession existingSession = request.getSession(false);
	    if(existingSession != null) {
	        existingSession.invalidate();
	    }
	    HttpSession session = request.getSession(true);
		
		
        Usuario u = new Usuario();
        UserController ctrl = new UserController();

        
        u.setUsuario(usuario);
        u.setClave(clave);
        u = ctrl.login(u);
        
        if (u == null) {
            session.setAttribute("errorMessage", "Credenciales incorrectas");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        } else {
        	RolController rolCtrl = new RolController();
        	Rol rol = rolCtrl.getOne(u.getRol());
            session.setAttribute("usuario", u);
            session.setAttribute("rol", rol.getNombre());
           
            
            response.sendRedirect(request.getContextPath() +"/");
        }
	}
	
}

