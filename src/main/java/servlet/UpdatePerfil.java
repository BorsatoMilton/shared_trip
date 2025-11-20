package servlet;

import java.io.IOException;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import entidades.Usuario;
import logic.UserController;


@WebServlet("/perfil")
public class UpdatePerfil extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private UserController usuarioCtrl = new UserController();
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    private static final Pattern TELEFONO_PATTERN = Pattern.compile(
            "^\\+?[0-9]{8,15}$"
    );
    private static final Pattern USUARIO_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._-]{3,20}$"
    );


    public UpdatePerfil() {
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
        Usuario logueado = (Usuario) session.getAttribute("usuario");
		try {
			if ("profile".equals(action)) {
				actualizarUsuario(request, logueado);
                session.setAttribute("mensaje", "Usuario actualizado con éxito");
			} else if ("password".equals(action)) {
				actualizarClave(request, logueado);
                session.setAttribute("mensaje", "Clave actualizada con éxito");
			} else {
				throw new Exception("No se especifico la acción");
			}
			
		}catch(Exception e) {
			session.setAttribute("error", "Error: " + e.getMessage());
			System.out.println("Error en editarUsuario: " + e.getMessage());
		}
		
		response.sendRedirect(request.getContextPath() + "/perfil");
	}


    private void actualizarUsuario(HttpServletRequest request, Usuario logueado) throws Exception {

        Integer id = ((Usuario) request.getSession().getAttribute("usuario")).getIdUsuario();

        if(id == null){
            throw new Exception("Valor de ID invalido");
        }

        String usuario = validarUsuario(request.getParameter("usuario"));
        String nombre = validarNombre(request.getParameter("nombre"));
        String apellido = validarApellido(request.getParameter("apellido"));
        String correo = validarEmail(request.getParameter("correo"));
        String telefono = validarTelefono(request.getParameter("telefono"));


        String clave = request.getParameter("clave");
        if (clave != null && !clave.trim().isEmpty()) {
            clave = validarClave(clave);
        } else {
            clave = null;
        }

        Integer rol = null;

        usuarioCtrl.actualizarUsuario(id, usuario, clave, nombre, apellido,
                correo, telefono, rol, logueado);
    }
	
	
	private boolean actualizarClave(HttpServletRequest request, Usuario logueado) throws Exception {
        int id = ((Usuario) request.getSession().getAttribute("usuario")).getIdUsuario();
		Usuario u = usuarioCtrl.getOneById(id);

		if (u == null) {
			throw new Exception("Usuario no encontrado");
		}

        if(logueado.getIdUsuario() != id) {
            throw new Exception("No puede cambiar la clave a otro usuario");
        }

        String clave = request.getParameter("clave");
        if (clave != null && !clave.trim().isEmpty()) {
            clave = validarClave(clave);
        } else {
            throw new Exception("La clave no puede estar vacia");
        }

        return usuarioCtrl.updatePassword(id, clave);
	}


    private String validarUsuario(String usuario) throws Exception {
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

    private String validarClave(String clave) throws Exception {
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

    private String validarNombre(String nombre) throws Exception {
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

    private String validarApellido(String apellido) throws Exception {
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

    private String validarEmail(String email) throws Exception {
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

    private String validarTelefono(String telefono) throws Exception {
        if (telefono == null || telefono.trim().isEmpty()) {
            throw new Exception("El teléfono es obligatorio");
        }

        telefono = telefono.trim().replaceAll("[\\s-]", "");

        if (!TELEFONO_PATTERN.matcher(telefono).matches()) {
            throw new Exception("Formato de teléfono inválido (8-15 dígitos)");
        }

        return telefono;
    }

}
