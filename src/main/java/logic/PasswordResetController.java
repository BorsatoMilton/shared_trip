package logic;

import data.PasswordResetDAO;
import entidades.PasswordReset;


public class PasswordResetController {
    private final PasswordResetDAO passwordResetDAO;

    public PasswordResetController() {
        this.passwordResetDAO = new PasswordResetDAO();
    }

    public PasswordReset validarToken(String token) {

        PasswordReset pr = passwordResetDAO.obtenerPorToken(token);

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