package servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import entidades.Usuario;
import entidades.Vehiculo;
import logic.UserController;
import logic.VehiculoController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@WebServlet("/vehiculos")
public class CRUDvehiculos extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(CRUDvehiculos.class);
    private final VehiculoController vehiculoCtrl = new VehiculoController();

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("usuario") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        Usuario usuario = (Usuario) session.getAttribute("usuario");
        String tipo_usuario = usuario.getNombreRol();

        try {
            LinkedList<Vehiculo> vehiculos;

            if ("admin".equals(tipo_usuario)) {
                vehiculos = vehiculoCtrl.getAll();
            } else if ("usuario".equals(tipo_usuario)) {
                vehiculos = vehiculoCtrl.getVehiculosUsuario(usuario);
            } else {
                vehiculos = new LinkedList<>();
                session.setAttribute("error", "No tiene permisos para ver vehículos");
            }

            request.setAttribute("vehiculos", vehiculos);
            request.getRequestDispatcher("misVehiculos.jsp").forward(request, response);

        } catch (Exception e) {
            logger.error("Error en doGet de vehiculos: ", e);
            session.setAttribute("error", "Error al cargar los vehículos");
            response.sendRedirect(request.getContextPath() + "/");
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("usuario") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        Usuario usuario = (Usuario) session.getAttribute("usuario");
        String action = request.getParameter("action");

        try {
            if ("update".equals(action)) {
                actualizarVehiculo(request, usuario);
                session.setAttribute("mensaje", "Vehículo actualizado con éxito");

            } else if ("delete".equals(action)) {
                eliminarVehiculo(request, usuario);
                session.setAttribute("mensaje", "Vehículo eliminado con éxito");

            } else if ("add".equals(action)) {
                crearVehiculo(request, usuario);
                session.setAttribute("mensaje", "Vehículo creado con éxito");

            } else {
                throw new Exception("Acción no válida");
            }

        } catch (Exception e) {
            session.setAttribute("error", e.getMessage());
            logger.error("Error en {} vehiculo: ", action, e);
        }

        response.sendRedirect(request.getContextPath() + "/vehiculos");
    }

    private void crearVehiculo(HttpServletRequest request, Usuario usuario) throws Exception {


        String patente = request.getParameter("patente");
        String modelo = request.getParameter("modelo");
        String anioStr = request.getParameter("anio");

        if (patente == null || patente.trim().isEmpty()) {
            throw new Exception("La patente es obligatoria");
        }

        if (modelo == null || modelo.trim().isEmpty()) {
            throw new Exception("El modelo es obligatorio");
        }

        if (anioStr == null || anioStr.trim().isEmpty()) {
            throw new Exception("El año es obligatorio");
        }


        patente = patente.trim().toUpperCase();
        if (!patente.matches("^[A-Z]{3}[0-9]{3}$") && // Formato viejo: ABC123
                !patente.matches("^[A-Z]{2}[0-9]{3}[A-Z]{2}$")) { // Formato nuevo: AB123CD
            throw new Exception("Formato de patente inválido. Use estilos como: ABC123 o AB123CD");
        }

        int anio;
        try {
            anio = Integer.parseInt(anioStr);
        } catch (NumberFormatException e) {
            throw new Exception("El año debe ser un número");
        }

        int anioActual = java.time.Year.now().getValue();
        if (anio < 1900 || anio > anioActual + 1) {
            throw new Exception("El año debe estar entre 1900 y " + (anioActual + 1));
        }

        if (modelo.length() < 2 || modelo.length() > 100) {
            throw new Exception("El modelo debe tener entre 2 y 100 caracteres");
        }

        vehiculoCtrl.crearVehiculo(patente, modelo, anio, usuario.getIdUsuario());
    }

    private void actualizarVehiculo(HttpServletRequest request, Usuario usuario) throws Exception {

        String idStr = request.getParameter("idVehiculo");
        if (idStr == null || idStr.trim().isEmpty()) {
            throw new Exception("ID de vehículo inválido");
        }

        int id;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            throw new Exception("ID de vehículo debe ser un número");
        }

        String patente = request.getParameter("patente");
        String modelo = request.getParameter("modelo");
        String anioStr = request.getParameter("anio");

        if (patente == null || patente.trim().isEmpty()) {
            throw new Exception("La patente es obligatoria");
        }

        if (modelo == null || modelo.trim().isEmpty()) {
            throw new Exception("El modelo es obligatorio");
        }

        if (anioStr == null || anioStr.trim().isEmpty()) {
            throw new Exception("El año es obligatorio");
        }

        patente = patente.trim().toUpperCase();
        if (!patente.matches("^[A-Z]{3}[0-9]{3}$") &&
                !patente.matches("^[A-Z]{2}[0-9]{3}[A-Z]{2}$")) {
            throw new Exception("Formato de patente inválido");
        }

        int anio;
        try {
            anio = Integer.parseInt(anioStr);
        } catch (NumberFormatException e) {
            throw new Exception("El año debe ser un número");
        }

        int anioActual = java.time.Year.now().getValue();
        if (anio < 1900 || anio > anioActual + 1) {
            throw new Exception("El año debe estar entre 1900 y " + (anioActual + 1));
        }

        if (modelo.length() < 2 || modelo.length() > 100) {
            throw new Exception("El modelo debe tener entre 2 y 100 caracteres");
        }

        vehiculoCtrl.actualizarVehiculo(id, patente, modelo, anio, usuario);
    }

    private void eliminarVehiculo(HttpServletRequest request, Usuario usuario) throws Exception {

        String idStr = request.getParameter("idVehiculo");
        if (idStr == null || idStr.trim().isEmpty()) {
            throw new Exception("ID de vehículo inválido");
        }

        int id;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            throw new Exception("ID de vehículo debe ser un número");
        }

        vehiculoCtrl.eliminarVehiculo(id, usuario);
    }


}


