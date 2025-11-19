package servlet;

import entidades.Rol;
import entidades.Usuario;
import logic.RolController;
import logic.UserController;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.LinkedList;

@WebServlet("/auth")
public class Auth extends HttpServlet {
    private static final long serialVersionUID = 1L;
    UserController userController = new UserController();

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("login".equals(action)) {
            login(request, response);
        }else if ("logout".equals(action)) {
            logout(request, response);
        }
    }


    // ---------------- LOGIN ----------------
    private void login(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String usuario = request.getParameter("usuario");
        String clave = request.getParameter("password");

        if (usuario == null || usuario.isBlank() ||
                clave == null || clave.isBlank()) {

            request.getSession().setAttribute("errorMessage", "Complete las credenciales");
            response.sendRedirect("login.jsp");
            return;
        }

        // Invalidar session anterior
        HttpSession old = request.getSession(false);
        if (old != null) old.invalidate();

        HttpSession session = request.getSession(true);


        Usuario u = new Usuario();
        u.setUsuario(usuario);
        u.setClave(clave);

        u = userController.login(u);

        if (u == null) {
            session.setAttribute("errorMessage", "Credenciales incorrectas");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        Rol rol = new RolController().getOne(u.getRol());

        session.setAttribute("usuario", u);
        session.setAttribute("rol", rol.getNombre());

        response.sendRedirect(request.getContextPath() + "/");
    }


    // ---------------- LOGOUT ----------------
    private void logout(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        response.sendRedirect(request.getContextPath() + "/");
    }
}