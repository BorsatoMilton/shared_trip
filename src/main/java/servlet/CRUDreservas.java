package servlet;

import entidades.Reserva;
import entidades.Rol;
import entidades.Usuario;
import entidades.Viaje;
import jakarta.mail.MessagingException;
import logic.ReservaController;
import logic.RolController;
import logic.UserController;
import logic.ViajeController;
import utils.Formatters;
import utils.MailService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import data.ViajeDAO;

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
    MailService mailService = new MailService();
    private final Formatters formatters = new Formatters();
    private static final Logger logger = LoggerFactory.getLogger(ViajeDAO.class);

    


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
                request.setAttribute("mensaje", "Reserva realizada con éxito");
            } else if ("cancelar".equals(action)) {
                cancelarReserva(request, usuario);
                request.setAttribute("mensaje", "Reserva cancelada con éxito");
            } else if ("validate".equals(action)) {
                validarReserva(request);
                request.setAttribute("mensaje", "Reserva validada");
            }
        } catch (Exception e) {
            request.setAttribute("error", "Error: " + e.getMessage());
            System.out.println("Error en operación: " + e.getMessage());
        }

        if ("reserve".equals(action)) {
            request.getRequestDispatcher("/").forward(request, response);
        } else if ("cancelar".equals(action)) {
            request.getRequestDispatcher( "misReservas.jsp").forward(request, response);
        } else if ("validate".equals(action)) {
            request.getRequestDispatcher("misViajes.jsp").forward(request, response);
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

        if(viaje.getConductor().getIdUsuario() != ((Usuario) request.getSession().getAttribute("usuario")).getIdUsuario()) {
            throw new Exception("No tienes permisos para validar reservas");
        }

        if(viaje.isCancelado()){
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
                if("CONFIRMADA".equalsIgnoreCase(reserva.getEstado())) {
                    throw new Exception("Esta reserva ya esta confirmada");
                }else if ("CANCELADA".equalsIgnoreCase(reserva.getEstado())) {
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

            int totalReservas = reserva.getCantidad_pasajeros_reservada();
            int cod_reserva = reserva.getCodigo_reserva();
            String datosViaje = formatters.formatDatosViaje(viaje);
            String datosChofer = formatters.formatDatosChofer(chofer, viaje.getVehiculo() != null ? viaje.getVehiculo().getPatente() : null);
            String datosPasajero = formatters.formatDatosPasajero(reserva.getPasajero(),reserva.getCantidad_pasajeros_reservada());

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

        } catch (MessagingException e) {
            System.err.println("Error enviando emails de notificación: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error preparando notificaciones: " + e.getMessage());
        }
    }

    private void enviarNotificacionesCancelacionReserva(Reserva reserva, Usuario usuario) {
        try {
            Viaje viaje = reserva.getViaje();
            Usuario chofer = viaje.getConductor();
            if (chofer == null) {
                logger.warn("Conductor es null en viaje ID: {}, obteniendo de la base de datos", viaje.getIdViaje());
                
                Viaje viajeCompleto = viajeController.getOne(viaje.getIdViaje());
                if (viajeCompleto != null && viajeCompleto.getConductor() != null) {
                    chofer = viajeCompleto.getConductor();
                    viaje.setConductor(chofer); 
                } else {
                    logger.error("No se pudo obtener el conductor para viaje ID: {}", viaje.getIdViaje());
                    return; 
                }
            }
            
            if (chofer == null || chofer.getCorreo() == null) {
                logger.error("No se puede enviar notificación: conductor o correo es null para viaje ID: {}", viaje.getIdViaje());
                return;
            }

            int nuevoTotalReservas = reserva.getCantidad_pasajeros_reservada();

            String datosViaje = formatters.formatDatosViaje(viaje);
            String datosChofer = formatters.formatDatosChofer(chofer, viaje.getVehiculo() != null ? viaje.getVehiculo().getPatente() : null);
            String datosPasajero = formatters.formatDatosPasajero(reserva.getPasajero(),reserva.getCantidad_pasajeros_reservada());


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

        } catch (MessagingException e) {
            System.err.println("Error enviando emails de cancelación: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error preparando notificaciones de cancelación: " + e.getMessage());
        }
    }

}