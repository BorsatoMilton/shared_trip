package servlet;

import entidades.Reserva;
import entidades.Rol;
import entidades.Usuario;
import entidades.Viaje;
import logic.ReservaController;
import logic.RolController;
import logic.UserController;
import logic.ViajeController;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.LinkedList;

@WebServlet("/reservas")
public class CRUDreservas extends HttpServlet {
    private static final long serialVersionUID = 1L;
    ViajeController viajeController = new ViajeController();
    ReservaController reservaController = new ReservaController();


    public CRUDreservas() {
        super();
        // TODO Auto-generated constructor stub
    }


    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("usuario") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        LinkedList<Reserva> reservas = reservaController.getReservasUsuario(usuario);

        request.getSession().setAttribute("misreservas", reservas);
        request.getRequestDispatcher("misReservas.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("usuario") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        String action = request.getParameter("action");

        try {
            if ("reserve".equals(action)) {
                reservar(request, usuario);
                session.setAttribute("mensaje", "Reserva realizada con éxito");
            } else if ("cancelar".equals(action)) {
                cancelarReserva(request, usuario);
                session.setAttribute("mensaje", "Reserva cancelada con éxito");
            } else if ("updateStatus".equals(action)) {
                actualizarEstadoReserva(request);
                session.setAttribute("mensaje", "Reserva actualizada");
            }
        } catch (Exception e) {
            session.setAttribute("error", "Error: " + e.getMessage());
            System.out.println("Error en operación: " + e.getMessage());
        }

        if ("reserve".equals(action)) {
            response.sendRedirect(request.getContextPath() + "/");
        } else if ("cancelar".equals(action)) {
            response.sendRedirect(request.getContextPath() + "/reservas");
        }

    }

    private void reservar(HttpServletRequest request, Usuario usuario) throws Exception {

        String viajeIdStr = request.getParameter("viajeId");
        String cantPasajerosStr = request.getParameter("cantPasajeros");

        if (viajeIdStr == null || viajeIdStr.trim().isEmpty()) {
            throw new Exception("Debe seleccionar un viaje");
        }

        if (cantPasajerosStr == null || cantPasajerosStr.trim().isEmpty()) {
            throw new Exception("Debe indicar la cantidad de pasajeros");
        }

        int viajeId;
        int cantPasajeros;

        try {
            viajeId = Integer.parseInt(viajeIdStr);
            cantPasajeros = Integer.parseInt(cantPasajerosStr);
        } catch (NumberFormatException e) {
            throw new Exception("Formato de número inválido");
        }

        if (cantPasajeros <= 0) {
            throw new Exception("La cantidad de pasajeros debe ser mayor a 0");
        }

        reservaController.nuevaReserva(viajeId, cantPasajeros, usuario.getIdUsuario());
    }

    private void cancelarReserva(HttpServletRequest request, Usuario usuario) throws Exception {

        String reservaIdStr = request.getParameter("reservaId");

        if (reservaIdStr == null || reservaIdStr.trim().isEmpty()) {
            throw new Exception("ID de reserva inválido");
        }

        int idReserva;
        try {
            idReserva = Integer.parseInt(reservaIdStr);
        } catch (NumberFormatException e) {
            throw new Exception("Formato de número inválido");
        }

        // Delegar al controller
        reservaController.cancelarReserva(idReserva, usuario.getIdUsuario());
    }

    private void actualizarEstadoReserva(HttpServletRequest request) {
        /*ReservaController reservaCtrl = new ReservaController();

        int idReserva = Integer.parseInt(request.getParameter("idReserva"));
        Reserva reserva = reservaCtrl.getOne(idReserva);
        int codigoCorrecto = reserva.getViaje().getCodigoValidacion();

        int codigoIngresado = Integer.parseInt(request.getParameter("codigo_validacion_usuario"));

        int intentos = reserva.getIntentos_codigo();

        if (intentos <= 0) {
            reservaCtrl.actualizarEstado(idReserva,"INVALIDADA" );
            return;
        }

        if (codigoIngresado == codigoCorrecto) {
            reservaCtrl.actualizarEstado(idReserva,"PAGADA" );

        } else {
            intentos--;
            reserva.setIntentos_codigo(intentos);


            if (intentos <= 0) {
                reservaCtrl.actualizarEstado(idReserva,"INVALIDADA" );
            }

        }
        reservaCtrl.updateEntity(reserva, idReserva);
        response.sendRedirect("misReservas");*/
        System.out.println("En construcción ...");
    }

}