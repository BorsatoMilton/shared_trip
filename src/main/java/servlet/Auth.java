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

        if (token == null || token.trim().isEmpty()) {
            session.setAttribute("error", "Token inválido");
            response.sendRedirect("/index.jsp");
            return;
        }
        try {
            PasswordReset pr = passwordResetController.validarToken(token);
            if (pr == null) {
                session.setAttribute("error", "Token ya utilizado o inexistente");
                response.sendRedirect("/index.jsp");
            }
            request.setAttribute("token", pr.getToken());
            request.getRequestDispatcher("/resetearClave.jsp").forward(request, response);
        } catch (Exception e) {
            session.setAttribute("error", e.getMessage());
            response.sendRedirect("/index.jsp");
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        HttpSession session = request.getSession();

        try {
            if ("login".equals(action)) {
                login(request, response);
            } else if ("logout".equals(action)) {
                logout(request, response);
            } else if ("recover".equals(action)) {
                recoverPasswordMail(request, response);
            } else if ("newPassword".equals(action)) {
                cambiarContrasena(request, response);
            } else {
                session.setAttribute("error", "Acción inválida");
                request.getRequestDispatcher("/index.jsp").forward(request, response);
            }
        }catch (Exception e) {
            session.setAttribute("error", e.getMessage());
            response.sendRedirect("/index.jsp");
            if("recover".equals(action)) {
                request.getRequestDispatcher("/recuperarClave.jsp").forward(request, response);
            }else if("newPassword".equals(action)) {
                request.getRequestDispatcher("/resetearClave.jsp").forward(request, response);
            }
        }

    }

    // ---------------------- LOGIN ----------------------
    private void login(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String usuario = request.getParameter("usuario");
        String clave = request.getParameter("password");

        if (usuario == null || usuario.isBlank() ||
                clave == null || clave.isBlank()) {

            request.getSession().setAttribute("error", "Complete las credenciales");
            response.sendRedirect("login.jsp");
            return;
        }

        HttpSession old = request.getSession(false);
        if (old != null) old.invalidate();

        HttpSession session = request.getSession(true);

        Usuario u = new Usuario();
        u.setUsuario(usuario);
        u.setClave(clave);

        u = userController.login(u);

        if (u == null) {
            session.setAttribute("error", "Credenciales incorrectas");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        Rol rol = new RolController().getOne(u.getRol());

        u.setClave(null);
        session.setAttribute("usuario", u);
        session.setAttribute("rol", rol.getNombre());

        response.sendRedirect(request.getContextPath() + "/");
    }

    // ---------------------- LOGOUT ----------------------
    private void logout(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        response.sendRedirect(request.getContextPath() + "/");
    }

    // ---------------------- RECUPERAR CLAVE MAIL ----------------------
    private void recoverPasswordMail(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String email = request.getParameter("email");
        HttpSession session = request.getSession();

        try {
            Usuario u = userController.getOneByEmail(email);
            if (u == null) {
                throw new Exception("Usuario no existente");
            }

            String token = generators.generarToken();
            passwordResetController.guardarToken(u.getIdUsuario(), token);
            mailService.recuperarClave(u, token);

            session.setAttribute("mensaje", "Se envio un mail a su casilla de correo para recuperar su cuenta");
            response.sendRedirect("/login.jsp");

        } catch (Exception e) {
            request.getSession().setAttribute("error", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/recuperarClave.jsp");
        }
    }

    // ---------------------- CAMBIAR CONTRASEÑA ----------------------
    private void cambiarContrasena(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        String token = request.getParameter("token");
        String nuevaPassword = request.getParameter("nuevaPassword");
        String confirmacionPassword = request.getParameter("confirmarPassword");
        HttpSession session = request.getSession();

        if (token == null || token.trim().isEmpty()) {
            session.setAttribute("error", "Token inexistente");
            response.sendRedirect("/index.jsp");
            return;
        }

        PasswordReset pr = passwordResetController.validarToken(token);
        if (pr == null) {
            session.setAttribute("error", "Token inválido");
            response.sendRedirect("/index.jsp");
            return;
        }

        if (nuevaPassword == null || nuevaPassword.trim().isEmpty()) {
            session.setAttribute("error", "La clave no puede estar vacia");
            request.setAttribute("token", token);
            request.getRequestDispatcher("/resetearClave.jsp").forward(request, response);
            return;
        }

        if (confirmacionPassword == null || confirmacionPassword.trim().isEmpty()) {
            session.setAttribute("error", "La confirmación de clave no puede estar vacia");
            request.setAttribute("token", token);
            request.getRequestDispatcher("/resetearClave.jsp").forward(request, response);
            return;
        }

        nuevaPassword = inputValidator.validarClave(nuevaPassword);
        confirmacionPassword = inputValidator.validarClave(confirmacionPassword);

        if (!nuevaPassword.equals(confirmacionPassword)) {
            session.setAttribute("error", "Las claves no coinciden");
            request.setAttribute("token", token);
            request.getRequestDispatcher("/resetearClave.jsp").forward(request, response);
            return;
        }

        if (pr.getUsuario() == null) {
            session.setAttribute("error", "El token corresponde a un usuario no existente");
            response.sendRedirect("/index.jsp");
            return;
        }

        Usuario u = userController.getOneById(pr.getUsuario().getIdUsuario());
        if (u == null) {
            session.setAttribute("error", "Usuario no existente");
            response.sendRedirect("/index.jsp");
            return;
        }

        userController.updatePassword(pr.getUsuario().getIdUsuario(), nuevaPassword);
        passwordResetController.marcarUtilizado(token);

        session.setAttribute("mensaje", "Contraseña actualizada correctamente");
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }
}