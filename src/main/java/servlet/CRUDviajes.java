package servlet;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import entidades.Reserva;
import entidades.Usuario;
import entidades.Vehiculo;
import entidades.Viaje;
import jakarta.mail.MessagingException;
import logic.ReservaController;
import logic.UserController;
import logic.VehiculoController;
import logic.ViajeController;
import utils.MailService;

@WebServlet("/viajes")
public class CRUDviajes extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final ViajeController viajeCtrl = new ViajeController();
    private final UserController usuarioCtrl = new UserController();
    private final VehiculoController vehiculoCtrl = new VehiculoController();
    private final ReservaController reservaCtrl = new ReservaController();
    private final MailService mailService = new MailService();

    public CRUDviajes() {
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


        String tipo = usuario.getNombreRol();

        LinkedList<Viaje> viajes = new LinkedList<>();
        LinkedList<Usuario> usuarios = null;
        LinkedList<Vehiculo> vehiculos = null;

        try {
            if ("admin".equalsIgnoreCase(tipo)) {
                viajes = viajeCtrl.getAll();
                usuarios = usuarioCtrl.getAll();
            } else if ("usuario".equalsIgnoreCase(tipo)) {
                viajes = viajeCtrl.getViajesUsuario(usuario);
                vehiculos = vehiculoCtrl.getVehiculosUsuario(usuario);
            }

            for (Viaje v : viajes) {
                if (v.getConductor() != null && v.getConductor().getIdUsuario() > 0) {
                    Usuario u = usuarioCtrl.getOneById(v.getConductor().getIdUsuario());
                    v.setConductor(u);
                }
            }

            request.setAttribute("viajes", viajes);
            request.setAttribute("usuarios", usuarios);
            request.setAttribute("vehiculos", vehiculos);

            request.getRequestDispatcher("misViajes.jsp").forward(request, response);

        } catch (Exception e) {
            session.setAttribute("error", "Error al cargar los viajes: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/");
        }

    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (usuario == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String action = request.getParameter("action");

        try {
            if ("update".equals(action)) {
                actualizarViaje(request, usuario);
                session.setAttribute("mensaje", "Viaje actualizado con éxito");

            } else if ("delete".equals(action)) {
                eliminarViaje(request, usuario);
                session.setAttribute("mensaje", "Viaje eliminado con éxito");

            } else if ("add".equals(action)) {
                crearViaje(request, usuario);
                session.setAttribute("mensaje", "Viaje creado con éxito");

            } else if ("cancelarViaje".equals(action)) {
                cancelarViaje(request, usuario);
                session.setAttribute("mensaje", "Viaje cancelado con éxito");
            }

        } catch (Exception e) {
            session.setAttribute("error", e.getMessage());
            System.out.println("Error en " + action + ": " + e.getMessage());
            e.printStackTrace();
        }

        response.sendRedirect(request.getContextPath() + "/viajes");
    }

    private void crearViaje(HttpServletRequest request, Usuario u) throws Exception {
        String fechaStr = request.getParameter("fecha");
        String lugaresStr = request.getParameter("lugares_disponibles");
        String origen = request.getParameter("origen");
        String destino = request.getParameter("destino");
        String precioStr = request.getParameter("precio_unitario");
        String lugarSalida = request.getParameter("lugar_salida");
        String vehiculoIdStr = request.getParameter("idVehiculo");

        if (fechaStr == null || fechaStr.trim().isEmpty()) {
            throw new Exception("La fecha es obligatoria");
        }

        if (lugaresStr == null || lugaresStr.trim().isEmpty()) {
            throw new Exception("Los lugares disponibles son obligatorios");
        }

        if (origen == null || origen.trim().isEmpty()) {
            throw new Exception("El origen es obligatorio");
        }

        if (destino == null || destino.trim().isEmpty()) {
            throw new Exception("El destino es obligatorio");
        }

        if (precioStr == null || precioStr.trim().isEmpty()) {
            throw new Exception("El precio es obligatorio");
        }

        if (lugarSalida == null || lugarSalida.trim().isEmpty()) {
            throw new Exception("El lugar de salida es obligatorio");
        }

        if (vehiculoIdStr == null || vehiculoIdStr.trim().isEmpty()) {
            throw new Exception("Debe seleccionar un vehículo");
        }

        int lugares;
        double precio;
        int vehiculoId;

        try {
            lugares = Integer.parseInt(lugaresStr);
        } catch (NumberFormatException e) {
            throw new Exception("Los lugares disponibles deben ser un número");
        }

        try {
            precio = Double.parseDouble(precioStr);
        } catch (NumberFormatException e) {
            throw new Exception("El precio debe ser un número válido");
        }

        try {
            vehiculoId = Integer.parseInt(vehiculoIdStr);
        } catch (NumberFormatException e) {
            throw new Exception("ID de vehículo inválido");
        }


        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        formato.setLenient(false);
        Date utilDate;

        try {
            utilDate = formato.parse(fechaStr);
        } catch (ParseException e) {
            throw new Exception("Formato de fecha inválido. Use AAAA-MM-DD");
        }

        java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
        LocalDate fechaViaje = sqlDate.toLocalDate();

        if (fechaViaje.isBefore(LocalDate.now())) {
            throw new Exception("La fecha del viaje no puede ser en el pasado");
        }

        if (lugares < 1) {
            throw new Exception("Debe haber al menos 1 lugar disponible");
        }

        if (precio < 0) {
            throw new Exception("El precio no puede ser negativo");
        }

        if (precio > 999999) {
            throw new Exception("El precio es demasiado alto");
        }

        origen = origen.trim();
        destino = destino.trim();

        if (origen.equalsIgnoreCase(destino)) {
            throw new Exception("El origen y destino no pueden ser iguales");
        }

        if (origen.length() < 2 || origen.length() > 100) {
            throw new Exception("El origen debe tener entre 2 y 100 caracteres");
        }

        if (destino.length() < 2 || destino.length() > 100) {
            throw new Exception("El destino debe tener entre 2 y 100 caracteres");
        }

        if (lugarSalida.length() < 2 || lugarSalida.length() > 200) {
            throw new Exception("El lugar de salida debe tener entre 3 y 200 caracteres");
        }

        viajeCtrl.crearViaje(sqlDate, lugares, origen, destino, precio,
                lugarSalida, vehiculoId, u);
    }

    private void actualizarViaje(HttpServletRequest request, Usuario usuario) throws Exception {


        String idStr = request.getParameter("idViaje");
        if (idStr == null || idStr.trim().isEmpty()) {
            throw new Exception("ID de viaje inválido");
        }

        int id;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            throw new Exception("ID de viaje debe ser un número");
        }

        String fechaStr = request.getParameter("fecha");
        String lugaresStr = request.getParameter("lugares_disponibles");
        String origen = request.getParameter("origen");
        String destino = request.getParameter("destino");
        String precioStr = request.getParameter("precio_unitario");
        String lugarSalida = request.getParameter("lugar_salida");
        String vehiculoIdStr = request.getParameter("idVehiculo");


        if (fechaStr == null || fechaStr.trim().isEmpty()) {
            throw new Exception("La fecha es obligatoria");
        }

        if (lugaresStr == null || lugaresStr.trim().isEmpty()) {
            throw new Exception("Los lugares disponibles son obligatorios");
        }

        if (origen == null || origen.trim().isEmpty()) {
            throw new Exception("El origen es obligatorio");
        }

        if (destino == null || destino.trim().isEmpty()) {
            throw new Exception("El destino es obligatorio");
        }

        if (precioStr == null || precioStr.trim().isEmpty()) {
            throw new Exception("El precio es obligatorio");
        }

        if (lugarSalida == null || lugarSalida.trim().isEmpty()) {
            throw new Exception("El lugar de salida es obligatorio");
        }

        if (vehiculoIdStr == null || vehiculoIdStr.trim().isEmpty()) {
            throw new Exception("Debe seleccionar un vehículo");
        }

        int lugares;
        double precio;
        int vehiculoId;

        try {
            lugares = Integer.parseInt(lugaresStr);
        } catch (NumberFormatException e) {
            throw new Exception("Los lugares disponibles deben ser un número");
        }

        try {
            precio = Double.parseDouble(precioStr);
        } catch (NumberFormatException e) {
            throw new Exception("El precio debe ser un número válido");
        }

        try {
            vehiculoId = Integer.parseInt(vehiculoIdStr);
        } catch (NumberFormatException e) {
            throw new Exception("ID de vehículo inválido");
        }


        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        formato.setLenient(false);
        Date utilDate;

        try {
            utilDate = formato.parse(fechaStr);
        } catch (ParseException e) {
            throw new Exception("Formato de fecha inválido. Use AAAA-MM-DD");
        }

        java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
        LocalDate fechaViaje = sqlDate.toLocalDate();

        if (fechaViaje.isBefore(LocalDate.now())) {
            throw new Exception("La fecha del viaje no puede ser en el pasado");
        }

        if (lugares < 1) {
            throw new Exception("Debe haber al menos 1 lugar disponible");
        }

        if (precio < 0) {
            throw new Exception("El precio no puede ser negativo");
        }

        if (precio > 999999) {
            throw new Exception("El precio es demasiado alto");
        }

        origen = origen.trim();
        destino = destino.trim();

        if (origen.equalsIgnoreCase(destino)) {
            throw new Exception("El origen y destino no pueden ser iguales");
        }

        if (origen.length() < 2 || origen.length() > 100) {
            throw new Exception("El origen debe tener entre 2 y 100 caracteres");
        }

        if (destino.length() < 2 || destino.length() > 100) {
            throw new Exception("El destino debe tener entre 2 y 100 caracteres");
        }

        if (lugarSalida.length() < 2 || lugarSalida.length() > 200) {
            throw new Exception("El lugar de salida debe tener entre 3 y 200 caracteres");
        }

        viajeCtrl.actualizarViaje(id, sqlDate, lugares, origen, destino,
                precio, lugarSalida, vehiculoId, usuario);
    }

    private void eliminarViaje(HttpServletRequest request, Usuario u) throws Exception {
        String idStr = request.getParameter("idViaje");
        if (idStr == null || idStr.trim().isEmpty()) {
            throw new Exception("ID de viaje inválido");
        }

        int id;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            throw new Exception("ID de viaje debe ser un número");
        }

        viajeCtrl.eliminarViaje(id, u);
    }

    private void cancelarViaje(HttpServletRequest request, Usuario u) throws Exception {
        String idStr = request.getParameter("viajeId");
        if (idStr == null || idStr.trim().isEmpty()) {
            throw new Exception("ID de viaje inválido");
        }

        int id;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            throw new Exception("ID de viaje debe ser un número");
        }

        Viaje viaje = viajeCtrl.cancelarViaje(id, u);

        enviarNotificacionesCancelacionViaje(viaje, u);
    }

    private void enviarNotificacionesCancelacionViaje(Viaje viaje, Usuario chofer) {
        try {

            LinkedList<Reserva> reservas = reservaCtrl.getReservasPorViaje(viaje.getIdViaje());

            String datosViaje = formatDatosViaje(viaje);
            String datosChofer = formatDatosChofer(chofer, viaje.getVehiculo().getPatente());

            int totalPasajeros = reservas.stream()
                    .mapToInt(Reserva::getCantidad_pasajeros_reservada)
                    .sum();

            mailService.notificarCancelacionViajeChofer(
                    chofer.getCorreo(),
                    datosViaje,
                    reservas.size(),
                    totalPasajeros
            );


            for (Reserva reserva : reservas) {
                Usuario pasajero = usuarioCtrl.getOneById(reserva.getId_pasajero_reserva());

                if (pasajero != null && pasajero.getCorreo() != null) {
                    mailService.notificarCancelacionViajeUsuarios(
                            pasajero.getCorreo(),
                            datosViaje,
                            datosChofer
                    );
                }
            }

        } catch (MessagingException e) {
            System.err.println("Error enviando emails de cancelación de viaje: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error preparando notificaciones de cancelación de viaje: " + e.getMessage());
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



}
