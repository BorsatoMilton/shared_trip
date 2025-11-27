package servlet;

import data.FeedbackDAO;
import data.exceptions.DataAccessException;
import entidades.Reserva;
import entidades.Usuario;
import entidades.Vehiculo;
import entidades.Viaje;
import jakarta.mail.MessagingException;
import logic.ReservaController;
import logic.ViajeController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.Formatters;
import services.MailService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;

@WebServlet("/reservas")
public class CRUDreservas extends HttpServlet {
    private static final long serialVersionUID = 1L;
    ReservaController reservaController = new ReservaController();
    ViajeController viajeController = new ViajeController();
    MailService mailService = MailService.getInstance();
    private final Formatters formatters = new Formatters();
    private static final Logger logger = LoggerFactory.getLogger(CRUDreservas.class);


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

        try {
            LinkedList<Reserva> reservas = reservaController.getReservasUsuario(usuario);
            session.setAttribute("misreservas", reservas);
        } catch (Exception e) {
            session.setAttribute("error", "Ocurrió un error al obtener las reservas.");
        }
        request.getRequestDispatcher("misReservas.jsp").forward(request, response);
    }


    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();

        if (session == null || session.getAttribute("usuario") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        Usuario usuario = (Usuario) session.getAttribute("usuario");
        String action = request.getParameter("action");
        String redirectPage = "/";

        try {
            if ("reserve".equals(action)) {
                reservar(request, usuario);
                session.setAttribute("mensaje", "Reserva realizada con éxito");
                redirectPage = "/";
            } else if ("cancelar".equals(action)) {
                cancelarReserva(request, usuario);
                session.setAttribute("mensaje", "Reserva cancelada con éxito");
                redirectPage = "/reservas";
            } else if ("validate".equals(action)) {
                validarReserva(request);
                session.setAttribute("mensaje", "Reserva validada");
                redirectPage = "/viajes";
            }
        } catch (Exception e) {
            session.setAttribute("error", "Error: " + e.getMessage());
            if ("reserve".equals(action)) {
                redirectPage = "/";
            } else if ("cancelar".equals(action)) {
                redirectPage = "/misReservas.jsp";
            } else if ("validate".equals(action)) {
                redirectPage = "/misViajes.jsp";
            }
        }
        response.sendRedirect(request.getContextPath() + redirectPage);
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

        Reserva reserva = reservaController.nuevaReserva(viajeId, cantPasajeros, usuario);
        enviarNotificacionesReserva(reserva, usuario);
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

        Reserva reserva = reservaController.cancelarReserva(idReserva, usuario.getIdUsuario());
        enviarNotificacionesCancelacionReserva(reserva, usuario);

    }

    private void validarReserva(HttpServletRequest request) throws Exception {
        String idViaje = request.getParameter("idViaje");
        String codigoStr = request.getParameter("codigo");

        if (idViaje == null || idViaje.trim().isEmpty()) {
            throw new Exception("ID de viaje inválido");
        }

        if (codigoStr == null || codigoStr.trim().isEmpty()) {
            throw new Exception("Código de validación requerido");
        }

        Integer codigoValidacion;
        try {
            codigoValidacion = Integer.parseInt(codigoStr);
        } catch (NumberFormatException e) {
            throw new Exception("Código de validación debe ser un número");
        }

        if (codigoValidacion < 1000 || codigoValidacion > 9999) {
            throw new Exception("Código de validación inválido");
        }

        int id = Integer.parseInt(idViaje);

        Viaje viaje = viajeController.getOne(id);
        if (viaje == null) {
            throw new Exception("El viaje no existe");
        }

        if (viaje.getConductor().getIdUsuario() != ((Usuario) request.getSession().getAttribute("usuario")).getIdUsuario()) {
            throw new Exception("No tienes permisos para validar reservas");
        }

        if (viaje.isCancelado()) {
            throw new Exception("El viaje esta cancelado");
        }

        LinkedList<Reserva> reservas = reservaController.getReservasPorViaje(id);

        if (reservas.isEmpty()) {
            throw new Exception("No existen reservas para este viaje");
        }

        Date ahora = new Date();
        if (viaje.getFecha().before(ahora)) {
            throw new Exception("El viaje ya se realizó.");
        }

        boolean reservaEncontrada = false;

        for (Reserva reserva : reservas) {
            if (reserva.getCodigo_reserva() == codigoValidacion) {
                if ("CONFIRMADA".equalsIgnoreCase(reserva.getEstado())) {
                    throw new Exception("Esta reserva ya esta confirmada");
                } else if ("CANCELADA".equalsIgnoreCase(reserva.getEstado())) {
                    throw new Exception("Esta reserva esta cancelada");
                }
                reservaEncontrada = true;

                reserva.setEstado("CONFIRMADA");
                reservaController.actualizarEstadoReserva(reserva);

                request.getSession().setAttribute("mensaje",
                        "Reserva validada correctamente. Código: " + codigoValidacion);

                break;
            }
        }

        if (!reservaEncontrada) {
            throw new Exception("No existe ninguna reserva con el código ingresado: " + codigoValidacion);
        }
    }


    private void enviarNotificacionesReserva(Reserva reserva, Usuario usuario) {
        try {
            Viaje viaje = reserva.getViaje();
            Usuario chofer = viaje.getConductor();
            Vehiculo vehiculo = viaje.getVehiculo();

            int totalReservas = reserva.getCantidad_pasajeros_reservada();
            int cod_reserva = reserva.getCodigo_reserva();
            String datosViaje = formatters.formatDatosViaje(viaje);
            String datosChofer = formatters.formatDatosChofer(chofer, vehiculo);
            String datosPasajero = formatters.formatDatosPasajero(reserva.getPasajero(), reserva.getCantidad_pasajeros_reservada());

            mailService.notificarReservaRealizadaUsuario(
                    usuario.getCorreo(),
                    datosViaje,
                    datosChofer,
                    totalReservas,
                    cod_reserva
            );

            mailService.notificarReservaRealizadaChofer(
                    chofer.getCorreo(),
                    datosViaje,
                    datosPasajero,
                    totalReservas
            );

        }catch (Exception e) {
            logger.error("Error al enviar las notificaciones de reserva: {}", e.getMessage());
        }
    }

    private void enviarNotificacionesCancelacionReserva(Reserva reserva, Usuario usuario) {
        try {
            Viaje viaje = reserva.getViaje();
            Usuario chofer = viaje.getConductor();
            Vehiculo vehiculo = viaje.getVehiculo();

            int nuevoTotalReservas = reserva.getCantidad_pasajeros_reservada();

            String datosViaje = formatters.formatDatosViaje(viaje);
            String datosChofer = formatters.formatDatosChofer(chofer, vehiculo);
            String datosPasajero = formatters.formatDatosPasajero(reserva.getPasajero(), reserva.getCantidad_pasajeros_reservada());


            mailService.notificarCancelacionReservaUsuario(
                    usuario.getCorreo(),
                    datosViaje,
                    datosChofer
            );

            mailService.notificarCancelacionReservaChofer(
                    chofer.getCorreo(),
                    datosViaje,
                    datosPasajero,
                    reserva.getCantidad_pasajeros_reservada(),
                    nuevoTotalReservas
            );

        } catch (Exception e) {
            logger.error("Error al enviar las notificaciones de cancelación de reserva: {}", e.getMessage());
        }
    }

}