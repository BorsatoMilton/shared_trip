package validators;

import java.util.regex.Pattern;

public class InputValidator {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    private static final Pattern TELEFONO_PATTERN = Pattern.compile(
            "^\\+?[0-9]{8,15}$"
    );
    private static final Pattern USUARIO_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._-]{3,20}$"
    );


    public String validarUsuario(String usuario) throws Exception {
        if (usuario == null || usuario.trim().isEmpty()) {
            throw new Exception("El nombre de usuario es obligatorio");
        }

        usuario = usuario.trim();

        if (!USUARIO_PATTERN.matcher(usuario).matches()) {
            throw new Exception("El usuario debe tener entre 3-20 caracteres " +
                    "(solo letras, números, puntos, guiones)");
        }

        return usuario;
    }

    public String validarClave(String clave) throws Exception {
        if (clave == null || clave.trim().isEmpty()) {
            throw new Exception("La contraseña es obligatoria");
        }

        if (clave.length() < 6) {
            throw new Exception("La contraseña debe tener al menos 6 caracteres");
        }

        if (clave.length() > 100) {
            throw new Exception("La contraseña es demasiado larga");
        }

        if (!clave.matches(".*[A-Za-z].*") || !clave.matches(".*[0-9].*")) {
            throw new Exception("La contraseña debe contener letras y números");
        }

        return clave;
    }

    public String validarNombre(String nombre) throws Exception {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new Exception("El nombre es obligatorio");
        }

        nombre = nombre.trim();

        if (nombre.length() < 2 || nombre.length() > 50) {
            throw new Exception("El nombre debe tener entre 2 y 50 caracteres");
        }

        if (!nombre.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")) {
            throw new Exception("El nombre solo puede contener letras");
        }

        return nombre;
    }

    public String validarApellido(String apellido) throws Exception {
        if (apellido == null || apellido.trim().isEmpty()) {
            throw new Exception("El apellido es obligatorio");
        }

        apellido = apellido.trim();

        if (apellido.length() < 2 || apellido.length() > 50) {
            throw new Exception("El apellido debe tener entre 2 y 50 caracteres");
        }

        if (!apellido.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")) {
            throw new Exception("El apellido solo puede contener letras");
        }

        return apellido;
    }

    public String validarEmail(String email) throws Exception {
        if (email == null || email.trim().isEmpty()) {
            throw new Exception("El email es obligatorio");
        }

        email = email.trim().toLowerCase();

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new Exception("Formato de email inválido");
        }

        if (email.length() > 100) {
            throw new Exception("El email es demasiado largo");
        }

        return email;
    }

    public String validarTelefono(String telefono) throws Exception {
        if (telefono == null || telefono.trim().isEmpty()) {
            throw new Exception("El teléfono es obligatorio");
        }

        telefono = telefono.trim().replaceAll("[\\s-]", "");

        if (!TELEFONO_PATTERN.matcher(telefono).matches()) {
            throw new Exception("Formato de teléfono inválido (8-15 dígitos)");
        }

        return telefono;
    }

    public int validarRol(String rolStr) throws Exception {
        if (rolStr == null || rolStr.trim().isEmpty()) {
            throw new Exception("El rol es obligatorio");
        }

        int rol;
        try {
            rol = Integer.parseInt(rolStr);
        } catch (NumberFormatException e) {
            throw new Exception("El rol debe ser un número");
        }

        if (rol < 1 || rol > 2) {
            throw new Exception("Rol inválido");
        }

        return rol;
    }

}
