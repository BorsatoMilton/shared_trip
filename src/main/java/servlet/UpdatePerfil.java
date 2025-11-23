package servlet;

import java.io.IOException;
import java.util.LinkedList;

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
import utils.InputValidator;


@WebServlet("/perfil")
public class UpdatePerfil extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final UserController usuarioCtrl = new UserController();
    private final RolController rolCtrl = new RolController();
    private final InputValidator inputValidator = new InputValidator();

    public UpdatePerfil() {
        super();
    }


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        if (request.getSession().getAttribute("usuario") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
        int idUsuario = usuario.getIdUsuario();
        
        Usuario usuarioNuevo = usuarioCtrl.getOneById(idUsuario);
        
        if (usuarioNuevo != null && usuarioNuevo.getNombreRol() == null) {
            LinkedList<Rol> roles = rolCtrl.getAll();
            for (Rol r : roles) {
                if (usuarioNuevo.getRol() == r.getIdRol()) {
                    usuarioNuevo.setNombreRol(r.getNombre());
                    break;
                }
            }
        }
        
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

             if (logueado != null) {
                 Usuario usuarioActualizado = usuarioCtrl.getOneById(logueado.getIdUsuario());
                 
                 if (usuarioActualizado != null && usuarioActualizado.getNombreRol() == null) {
                     LinkedList<Rol> roles = rolCtrl.getAll();
                     for (Rol r : roles) {
                         if (usuarioActualizado.getRol() == r.getIdRol()) {
                             usuarioActualizado.setNombreRol(r.getNombre());
                             break;
                         }
                     }
                 }
                 session.setAttribute("usuario", usuarioActualizado);
             }

         } catch (Exception e) {
             session.setAttribute("error", "Error: " + e.getMessage());
             System.out.println("Error en editarUsuario: " + e.getMessage());
         }

         response.sendRedirect(request.getContextPath() + "/perfil");
     }


    private void actualizarUsuario(HttpServletRequest request, Usuario logueado) throws Exception {

        Integer id = ((Usuario) request.getSession().getAttribute("usuario")).getIdUsuario();

        if (id == null) {
            throw new Exception("Valor de ID invalido");
        }

        String usuario = inputValidator.validarUsuario(request.getParameter("usuario"));
        String nombre = inputValidator.validarNombre(request.getParameter("nombre"));
        String apellido = inputValidator.validarApellido(request.getParameter("apellido"));
        String correo = inputValidator.validarEmail(request.getParameter("correo"));
        String telefono = inputValidator.validarTelefono(request.getParameter("telefono"));


        String clave = request.getParameter("clave");
        if (clave != null && !clave.trim().isEmpty()) {
            clave = inputValidator.validarClave(clave);
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

        if (logueado.getIdUsuario() != id) {
            throw new Exception("No puede cambiar la clave a otro usuario");
        }

        String clave = request.getParameter("clave");
        if (clave != null && !clave.trim().isEmpty()) {
            clave = inputValidator.validarClave(clave);
        } else {
            throw new Exception("La clave no puede estar vacia");
        }

        return usuarioCtrl.updatePassword(id, clave);
    }

}
