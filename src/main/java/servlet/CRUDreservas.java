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
import utils.MailService;

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
            } else if ("validate".equals(action)) {
                validarReserva(request);
                session.setAttribute("mensaje", "Reserva validada");
            }
        } catch (Exception e) {
            session.setAttribute("error", "Error: " + e.getMessage());
            System.out.println("Error en operación: " + e.getMessage());
        }

        if ("reserve".equals(action)) {
            response.sendRedirect(request.getContextPath() + "/");
        } else if ("cancelar".equals(action)) {
            response.sendRedirect(request.getContextPath() + "/reservas");
        } else if ("validate".equals(action)) {
            response.sendRedirect(request.getContextPath() + "/viajes");
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

        Reserva reserva = reservaController.nuevaReserva(viajeId, cantPasajeros, usuario.getIdUsuario());

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
            String datosViaje = formatDatosViaje(viaje);
            String datosChofer = formatDatosChofer(chofer, viaje.getVehiculo().getPatente());
            String datosPasajero = formatDatosPasajero(usuario, reserva.getCantidad_pasajeros_reservada());

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

            int nuevoTotalReservas = reserva.getCantidad_pasajeros_reservada();

            String datosViaje = formatDatosViaje(viaje);
            String datosChofer = formatDatosChofer(chofer, viaje.getVehiculo().getPatente());
            String datosPasajero = formatDatosPasajero(usuario, reserva.getCantidad_pasajeros_reservada());


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

    private String formatDatosViaje(Viaje viaje) {
        if (viaje == null) {
            return "Información del viaje no disponible";
        }

        String origen = viaje.getOrigen() != null ? viaje.getOrigen() : "No especificado";
        String destino = viaje.getDestino() != null ? viaje.getDestino() : "No especificado";
        String fecha = viaje.getFecha() != null ? viaje.getFecha().toString() : "No especificado";
        String precio = viaje.getPrecio_unitario() != null ? String.valueOf(viaje.getPrecio_unitario()) : "No especificado";
        String lugar_salida = viaje.getLugar_salida() != null ? viaje.getLugar_salida() : "No especificado";

        return String.format(
                "Origen: %s<br>Destino: %s<br>Lugar de Salida: %s<br>Fecha: %s<br>Precio por asiento: $%s",
                origen, destino, lugar_salida, fecha, precio
        );
    }

    private String formatDatosChofer(Usuario chofer, String patente) {
        if (chofer == null) {
            return "Información del chofer no disponible";
        }

        String nombreCompleto = (chofer.getNombre() != null ? chofer.getNombre() : "No especificado")
                + (chofer.getApellido() != null ? " " + chofer.getApellido() : "");
        String telefono = chofer.getTelefono() != null ? chofer.getTelefono() : "No especificado";
        String correo = chofer.getCorreo() != null ? chofer.getCorreo() : "No especificado";
        String vehiculo = patente != null ? patente : "No especificado";

        return String.format(
                "Nombre: %s<br>Teléfono: %s<br>Email: %s<br>Vehículo: %s",
                nombreCompleto, telefono, correo, vehiculo
        );
    }

    private String formatDatosPasajero(Usuario pasajero, int cantPasajeros) {
        if (pasajero == null) {
            return String.format("Asientos reservados: %d", cantPasajeros);
        }

        String nombre = pasajero.getNombre() != null ? pasajero.getNombre() : "No especificado";
        String apellido = pasajero.getApellido() != null ? pasajero.getApellido() : "";
        String telefono = pasajero.getTelefono() != null ? pasajero.getTelefono() : "No especificado";
        String correo = pasajero.getCorreo() != null ? pasajero.getCorreo() : "No especificado";

        return String.format(
                "Nombre: %s %s<br>Teléfono: %s<br>Email: %s<br>Asientos reservados: %d",
                nombre, apellido, telefono, correo, cantPasajeros
        );
    }

}