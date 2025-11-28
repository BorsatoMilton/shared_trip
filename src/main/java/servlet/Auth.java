package servlet;

import entidades.PasswordReset;
import entidades.Rol;
import entidades.Usuario;
import logic.PasswordResetController;
import logic.RolController;
import logic.UserController;
import utils.Generators;
import utils.InputValidator;
import services.MailService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/auth")
public class Auth extends HttpServlet {
    private static final long serialVersionUID = 1L;
    UserController userController = new UserController();
    MailService mailService = MailService.getInstance();
    Generators generators = new Generators();
    PasswordResetController passwordResetController = new PasswordResetController();
    InputValidator inputValidator = new InputValidator();

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String token = request.getParameter("t");
        HttpSession session = request.getSession();
        String redirectPage = "/index.jsp";

        if (token == null || token.trim().isEmpty()) {
            session.setAttribute("error", "Token inválido");
            response.sendRedirect(request.getContextPath() + redirectPage);
            return;
        }

        try {
            PasswordReset pr = passwordResetController.validarToken(token);
            if (pr == null) {
                session.setAttribute("error", "Token ya utilizado o inexistente");
                response.sendRedirect(request.getContextPath() + redirectPage);
                return;
            }
            request.setAttribute("token", pr.getToken());
            request.getRequestDispatcher("/resetearClave.jsp").forward(request, response);
        } catch (Exception e) {
            session.setAttribute("error", e.getMessage());
            response.sendRedirect(request.getContextPath() + redirectPage);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        HttpSession session = request.getSession();
        String redirectPage = "/";
        boolean forward = false;

        try {
            if ("login".equals(action)) {
                redirectPage = login(request);
            } else if ("logout".equals(action)) {
                redirectPage = logout(request);
            } else if ("recover".equals(action)) {
                redirectPage = recoverPasswordMail(request);
            } else if ("newPassword".equals(action)) {
                redirectPage = cambiarContrasena(request);
                forward = true;
            } else {
                session.setAttribute("error", "Acción inválida");
                redirectPage = "/index.jsp";
            }

        } catch (Exception e) {
            session.setAttribute("error", e.getMessage());

            if ("login".equals(action) || "logout".equals(action)) {
                redirectPage = "/login.jsp";
            } else if ("recover".equals(action)) {
                redirectPage = "/recuperarClave.jsp";
            } else if ("newPassword".equals(action)) {
                redirectPage = "/resetearClave.jsp";
                request.setAttribute("token", request.getParameter("token"));
                forward = true;
            } else {
                redirectPage = "/index.jsp";
            }
        }

        if (forward) {
            request.getRequestDispatcher(redirectPage).forward(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + redirectPage);
        }
    }

    // ---------------------- LOGIN ----------------------
    private String login(HttpServletRequest request) throws Exception {
        String usuario = request.getParameter("usuario");
        String clave = request.getParameter("password");

        if (usuario == null || usuario.isBlank() || clave == null || clave.isBlank()) {
            throw new Exception("Complete las credenciales");
        }

        Usuario u = new Usuario();
        u.setUsuario(usuario);
        u.setClave(clave);

        u = userController.login(u);

        if (u == null) {
            throw new Exception("Credenciales incorrectas");
        }

        HttpSession old = request.getSession(false);
        if (old != null) old.invalidate();
        HttpSession session = request.getSession();

        //Rol rol = new RolController().getOne(u.getRol());

        u.setClave(null);
        session.setAttribute("usuario", u);
        //session.setAttribute("rol", rol.getNombre());

        return "/";
    }

    // ---------------------- LOGOUT ----------------------
    private String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "/";
    }

    // ---------------------- RECUPERAR CLAVE MAIL ----------------------
    private String recoverPasswordMail(HttpServletRequest request) throws Exception {

        String email = request.getParameter("email");
        HttpSession session = request.getSession();

        if (email == null || email.trim().isEmpty()) {
            throw new Exception("Email requerido");
        }

        Usuario u = userController.getOneByEmail(email);
        if (u == null) {
            throw new Exception("Usuario no existente");
        }

        String token = generators.generarToken();
        passwordResetController.guardarToken(u.getIdUsuario(), token);
        mailService.recuperarClave(u, token);

        session.setAttribute("mensaje", "Se envió un mail a su casilla de correo para recuperar su cuenta");
        return "/login.jsp";
    }

    // ---------------------- CAMBIAR CONTRASEÑA ----------------------
    private String cambiarContrasena(HttpServletRequest request) throws Exception {

        String token = request.getParameter("token");
        String nuevaPassword = request.getParameter("nuevaPassword");
        String confirmacionPassword = request.getParameter("confirmarPassword");
        HttpSession session = request.getSession();

        if (token == null || token.trim().isEmpty()) {
            throw new Exception("Token inexistente");
        }

        PasswordReset pr = passwordResetController.validarToken(token);
        if (pr == null) {
            throw new Exception("Token inválido");
        }

        if (nuevaPassword == null || nuevaPassword.trim().isEmpty()) {
            request.setAttribute("token", token);
            session.setAttribute("error", "La clave no puede estar vacía");
            return "/resetearClave.jsp";
        }

        if (confirmacionPassword == null || confirmacionPassword.trim().isEmpty()) {
            request.setAttribute("token", token);
            session.setAttribute("error", "La confirmación de clave no puede estar vacía");
            return "/resetearClave.jsp";
        }

        nuevaPassword = inputValidator.validarClave(nuevaPassword);
        confirmacionPassword = inputValidator.validarClave(confirmacionPassword);

        if (!nuevaPassword.equals(confirmacionPassword)) {
            request.setAttribute("token", token);
            session.setAttribute("error", "Las claves no coinciden");
            return "/resetearClave.jsp";
        }

        if (pr.getUsuario() == null) {
            throw new Exception("El token corresponde a un usuario no existente");
        }

        Usuario u = userController.getOneById(pr.getUsuario().getIdUsuario());
        if (u == null) {
            throw new Exception("Usuario no existente");
        }

        userController.updatePassword(pr.getUsuario().getIdUsuario(), nuevaPassword);
        passwordResetController.marcarUtilizado(token);

        session.setAttribute("mensaje", "Contraseña actualizada correctamente");
        return "/login.jsp";
    }
}
