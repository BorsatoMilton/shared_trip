package logic;

import java.security.SecureRandom;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import data.PasswordResetDAO;
import data.UserDAO;
import entidades.PasswordReset;
import entidades.Usuario;

public class PasswordResetController {
    private static final Logger logger = LoggerFactory.getLogger(PasswordResetController.class);
    private final PasswordResetDAO passwordResetDAO;

    public PasswordResetController() {
        this.passwordResetDAO = new PasswordResetDAO();
    }

    public PasswordReset validarToken(String token) {

        PasswordReset pr = passwordResetDAO.obtenerPorToken(token);

        if (pr == null) {
            logger.warn("Intento de validación con token inválido o expirado: {}", token);
        }

        return pr;
    }

    public void guardarToken(int id_usuario, String token) throws Exception {
        if (token == null || token.trim().isEmpty()) {
            throw new Exception("Token no proporcionado");
        }
        passwordResetDAO.invalidarTokensPrevios(id_usuario);

        passwordResetDAO.guardarToken(id_usuario, token);
    }

    public void marcarUtilizado(String token) throws Exception {
        if (token == null || token.trim().isEmpty()) {
            throw new Exception("Token no proporcionado");
        }
        passwordResetDAO.marcarComoUtilizado(token);
    }

}