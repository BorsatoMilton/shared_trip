package logic;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;

import data.ReservaDAO;
import entities.Reserva;
import entities.Usuario;
import entities.Viaje;

public class ReservaController {
    private final ReservaDAO reservaDAO;
    private final ViajeController viajeController;
    
    public ReservaController() {
        this.reservaDAO = new ReservaDAO();
        this.viajeController = new ViajeController();
    }

    public LinkedList<Reserva> getAllReservas() {
        return this.reservaDAO.getAll();
    }

    public Reserva getByToken(String token) {
        return this.reservaDAO.getByToken(token);
    }

    public LinkedList<Reserva> getReservasUsuario(Usuario u) {
        LinkedList<Reserva> reservas = this.reservaDAO.getByUser(u);
        return reservas;
    }

    public Reserva cancelarReserva(int idReserva, Usuario usuario) throws Exception {

        Reserva reserva = reservaDAO.getByReserva(idReserva);
        if (reserva == null) {
            throw new Exception("La reserva no existe");
        }

        if (reserva.getPasajero().getIdUsuario() != usuario.getIdUsuario() && "usuario".equals(usuario.getNombreRol())) {
            throw new Exception("No tiene permisos para cancelar esta reserva");
        }

        if (reserva.isReserva_cancelada()) {
            throw new Exception("La reserva ya está cancelada");
        }

        if ("CONFIRMADA".equals(reserva.getEstado())) {
            throw new Exception("No se puede cancelar esta reserva, ya esta CONFIRMADA");
        }

        Viaje viaje = reserva.getViaje();
        LocalDate fechaViaje = viaje.getFecha().toLocalDate();

        if (fechaViaje.isBefore(LocalDate.now())) {
            throw new Exception("No se puede cancelar una reserva de un viaje que ya pasó");
        }

        if (!reservaDAO.cancelarReserva(idReserva)) {
            throw new Exception("Error al cancelar la reserva en la base de datos");
        }

        int cantidadPasajeros = reserva.getCantidad_pasajeros_reservada();
        viajeController.actualizarCantidad(viaje.getIdViaje(), cantidadPasajeros * (-1));
        return reserva;
    }

    public LinkedList<Reserva> getReservasPorViaje(int idViaje, boolean all) throws Exception {
        if (idViaje < 0) {
            throw new Exception("ID de viaje inválido");
        }
        return reservaDAO.getReservasByViaje(idViaje, all);
    }

    public Reserva nuevaReserva(int viajeId, int cantPasajeros, Usuario usuario) throws Exception {

        Viaje viaje = viajeController.getOne(viajeId);
        if (viaje == null) {
            throw new Exception("El viaje seleccionado no existe");
        }

        if (viaje.getConductor().getIdUsuario() == usuario.getIdUsuario()) {
            throw new Exception("No puede reservar en su propio viaje");
        }

        if (viaje.isCancelado()) {
            throw new Exception("No se puede reservar en un viaje cancelado");
        }

        LocalDate fechaViaje = viaje.getFecha().toLocalDate();
        LocalDate hoy = LocalDate.now();

        if (fechaViaje.isBefore(hoy)) {
            throw new Exception("No se puede reservar en un viaje que ya pasó");
        }

        if (viaje.getLugares_disponibles() < cantPasajeros) {
            throw new Exception("Solo quedan " + viaje.getLugares_disponibles() +
                    " lugares disponibles");
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fecha = new Date();
        String fechaString = sdf.format(fecha);


        LinkedList<Reserva> reservas = reservaDAO.getReservasByViaje(viajeId, false);

        int codigo_verificacion;
        boolean repetido;

        do {
            codigo_verificacion = (int) (Math.random() * 9000) + 1000;
            repetido = false;

            for (Reserva r : reservas) {
                if (r.getCodigo_reserva() == codigo_verificacion) {
                    repetido = true;
                    break;
                }
            }
        } while (repetido);
        Reserva r = new Reserva(fechaString, cantPasajeros, false, viaje, usuario, codigo_verificacion);

        reservaDAO.add(r);

        viajeController.actualizarCantidad(viajeId, cantPasajeros);

        return r;
    }

    public void actualizarEstadoReserva(Reserva reserva) throws Exception {
        if (reserva == null) {
            throw new Exception("La reserva no existe");
        }
        reservaDAO.actualizarEstado(reserva.getIdReserva(), reserva.getEstado());
    }

    public void eliminarReserva(int idReserva, Usuario usuario) throws Exception {

        Reserva reserva = reservaDAO.getByReserva(idReserva);
        if (reserva == null) {
            throw new Exception("La reserva no existe");
        }

        if (reserva.getPasajero().getIdUsuario() != usuario.getIdUsuario() && "usuario".equals(usuario.getNombreRol())) {
            throw new Exception("No tiene permisos para eliminar esta reserva");
        }

        if ("EN PROCESO".equals(reserva.getEstado())) {
            throw new Exception("No se puede cancelar esta reserva, esta en proceso, primero cancelala.");
        }

        if (!reservaDAO.delete(idReserva)) {
            throw new Exception("Error al eliminar la reserva en la base de datos");
        }

    }


    public int obtenerCantidad(int idReserva) {
        return this.reservaDAO.obtenerCantidad(idReserva);
    }

    public void actualizarEstado(int idReserva, String nuevoEstado) {
        this.reservaDAO.actualizarEstado(idReserva, nuevoEstado);

    }

    public void updateEntity(Reserva reserva, int idReserva) {
        this.reservaDAO.update(reserva, idReserva);
    }
    
    public double getIngresosTotales() {
            return reservaDAO.calcularIngresosTotales();
        }

    public double getIngresosMesActual() {
            return reservaDAO.calcularIngresosMesActual();
    }

    public double getPromedioPorReserva() {
            return reservaDAO.calcularPromedioPorReserva();
        }
    
//    public void pruebaExtremaReservasConfirmadas() {
//    	this.reservaDAO.pruebaExtremaReservasConfirmadas();
//    }
    
    public LinkedList<Reserva> obtenerReservasRecientes(int limite) {
    	return reservaDAO.obtenerReservasRecientes(limite);
    }

    public Map<String, Object> getEstadisticasReservas() {
            return reservaDAO.obtenerEstadisticasReservas();
    }
    
}