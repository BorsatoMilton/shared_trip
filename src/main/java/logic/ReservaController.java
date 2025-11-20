package logic;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.LinkedList;

import data.ReservaDAO;
import entidades.Reserva;
import entidades.Usuario;
import entidades.Viaje;

public class ReservaController {
    private final ReservaDAO reservaDAO;
    private final ViajeController viajeController;

    public ReservaController() {
        this.reservaDAO = new ReservaDAO();
        this.viajeController = new ViajeController();
    }

    public Reserva nuevaReserva(int viajeId, int cantPasajeros, int idUsuario) throws Exception {

        Viaje viaje = viajeController.getOne(viajeId);
        if (viaje == null) {
            throw new Exception("El viaje seleccionado no existe");
        }

        if (viaje.getConductor().getIdUsuario() == idUsuario) {
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

        Reserva r = new Reserva(fechaString, cantPasajeros, false, viaje, idUsuario, "EN PROCESO", 3);

        reservaDAO.add(r);

        viajeController.actualizarCantidad(viajeId, cantPasajeros);

        return r;
    }

    public LinkedList<Reserva> getReservasUsuario(Usuario u) {
        LinkedList<Reserva> reservas = this.reservaDAO.getByUser(u);
        return reservas;
    }


    public Reserva getOne(int id) {
        return this.reservaDAO.getByReserva(id);
    }

    public void actualizarEstado(int idReserva, String nuevoEstado) {
        this.reservaDAO.actualizarEstado(idReserva, nuevoEstado);

    }

    public void updateEntity(Reserva reserva, int idReserva) {
        this.reservaDAO.update(reserva, idReserva);
    }

    public Reserva cancelarReserva(int idReserva, int idUsuario) throws Exception {

        Reserva reserva = reservaDAO.getByReserva(idReserva);
        if (reserva == null) {
            throw new Exception("La reserva no existe");
        }

        if (reserva.getId_pasajero_reserva() != idUsuario) {
            throw new Exception("No tiene permisos para cancelar esta reserva");
        }

        if (reserva.isReserva_cancelada()) {
            throw new Exception("La reserva ya está cancelada");
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

    public int obtenerCantidad(int idReserva) {
        return this.reservaDAO.obtenerCantidad(idReserva);
    }
}