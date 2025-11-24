package servlet;

import entidades.PasswordReset;
import entidades.Reserva;
import entidades.Rol;
import entidades.Usuario;
import jakarta.mail.MessagingException;
import logic.PasswordResetController;
import logic.RolController;
import logic.UserController;
import utils.Generators;
import utils.InputValidator;
import utils.MailService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.rmi.server.ExportException;
import java.util.LinkedList;

@WebServlet("/auth")
public class Auth extends HttpServlet {
    private static final long serialVersionUID = 1L;
    UserController userController = new UserController();
    MailService mailService = new MailService();
    Generators generators = new Generators();
    PasswordResetController passwordResetController = new PasswordResetController();
    InputValidator inputValidator = new InputValidator();

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String token = request.getParameter("t");

        if (token == null || token.trim().isEmpty()) {
            request.setAttribute("error", "Token inválido");
            request.getRequestDispatcher("/index.jsp").forward(request, response);
            return;
        }

        try {
            PasswordReset pr = passwordResetController.validarToken(token);
            System.out.println("Token 1: " + token);
            request.setAttribute("token", pr.getToken());
            request.getRequestDispatcher("/resetearClave.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/index.jsp").forward(request, response);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("login".equals(action)) {
            login(request, response);
        } else if ("logout".equals(action)) {
            logout(request, response);
        } else if ("recover".equals(action)) {
            try {
                recoverPasswordMail(request, response);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if ("newPassword".equals(action)) {
            try {
                newPassword(request, response);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            request.setAttribute("error", "Acción inválida");
            request.getRequestDispatcher("/index.jsp").forward(request, response);
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
            request.setAttribute("errorMessage", "Credenciales incorrectas");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        Rol rol = new RolController().getOne(u.getRol());

        u.setClave(null);
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

    // ---------------------- RECUPERAR CLAVE MAIL ---------------------------
    private void recoverPasswordMail(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String email = request.getParameter("email");

        Usuario u = userController.getOneByEmail(email);

        if (u == null) {
            request.setAttribute("errorMessage", "No existe una cuenta asociada a ese email");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }

        String token = generators.generarToken();
        u.setClave(null);

        passwordResetController.addPasswordReset(u.getIdUsuario(), token);

        mailService.recuperarClave(u, token);

        request.setAttribute("mensaje", "Se envio un mail a su casilla de correo para recuperar su cuenta");
        request.getRequestDispatcher("/login.jsp").forward(request, response);

    }

    private void newPassword(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        String token = request.getParameter("token");
        if(token == null || token.trim().isEmpty()){
            request.setAttribute("error", "Token inexistente");
        }
        System.out.println("Token: " + token);
        PasswordReset pr = passwordResetController.validarToken(token);
        if (pr == null) {
            request.setAttribute("error", "Token inválido");
            request.getRequestDispatcher("/index.jsp").forward(request, response);
        }

        String nuevaPassword = request.getParameter("nuevaPassword");
        String confirmacionPassword = request.getParameter("confirmarPassword");


        if (nuevaPassword != null && !nuevaPassword.trim().isEmpty()) {
            nuevaPassword = inputValidator.validarClave(nuevaPassword);
        } else {
            throw new Exception("La clave no puede estar vacia");
        }
        if (confirmacionPassword != null && !confirmacionPassword.trim().isEmpty()) {
            confirmacionPassword = inputValidator.validarClave(confirmacionPassword);
        }else {
            throw new Exception("La clave no puede estar vacia");
        }

        if(!nuevaPassword.equals(confirmacionPassword)){
            throw new Exception("Las claves no coinciden");
        }

        if(pr.getUsuario()==null){
            throw new Exception("El token corresponde a un usuario no existente");
        }
        Usuario u = userController.getOneById(pr.getUsuario().getIdUsuario());

        if(u==null){
            throw new Exception("Usuario no existente");
        }

        userController.updatePassword(pr.getUsuario().getIdUsuario(), nuevaPassword);
        passwordResetController.marcarUtilizado(token);

        request.setAttribute("mensaje", "Contraseña actualizada correctamente");
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }
}