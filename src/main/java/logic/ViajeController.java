package logic;

import java.sql.Date;
import java.time.LocalDate;
import java.util.LinkedList;

import data.*;
import entidades.*;
import utils.DataAccessException;

public class ViajeController {
    private final ViajeDAO viajeDAO;
    private final VehiculoDAO vehiculoDAO;

    public ViajeController() {
        this.viajeDAO = new ViajeDAO();
        this.vehiculoDAO = new VehiculoDAO();
    }

    public LinkedList<Viaje> getAll() throws Exception {
        LinkedList<Viaje> viajes;
        try {
            viajes = viajeDAO.getAll();
        } catch (DataAccessException e) {
            throw new Exception("No se pudieron obtener los viajes. Intente más tarde.");
        }

        return viajes;
    }


    public LinkedList<Viaje> getAllBySearch(String origen, String destino, String fecha) throws Exception {
        LinkedList<Viaje> viajes;
        try {
            viajes = viajeDAO.getAllBySearch(origen, destino, fecha);
        } catch (DataAccessException e) {
            throw new Exception("No se pudieron obtener los viajes. Intente más tarde.");
        }

        return viajes;
    }

    public Viaje getOne(int id) throws Exception {
        Viaje viaje;
        try {
            viaje = this.viajeDAO.getByViaje(id);
        } catch (DataAccessException e) {
            throw new Exception("No se pudo obtener el viaje. Intente más tarde.");
        }
        return viaje;
    }

    public LinkedList<Viaje> getViajesUsuario(Usuario u) throws Exception {
        LinkedList<Viaje> viajes;
        try {
            viajes = this.viajeDAO.getByUser(u);
        } catch (DataAccessException e) {
            throw new Exception("No se pudieron obtener los viajes del usuario. Intente más tarde.");
        }

        return viajes;
    }

    public void actualizarCantidad(int idViaje, int cantidad) throws Exception{
        Viaje viaje;
        try {
            viaje = this.getOne(idViaje);
        } catch (DataAccessException e) {
            throw new Exception("No se pudo obtener el viaje. Intente más tarde.");
        }

        int nueva_cant = viaje.getLugares_disponibles() - (cantidad);

        try {
            this.viajeDAO.updateCantidad(idViaje, nueva_cant);
        } catch (DataAccessException e) {
            throw new Exception("No se pudo obtener el viaje. Intente más tarde.");
        }
    }

    public void actualizarViaje(int idViaje, Date fecha, int lugares, String origen,
                                String destino, double precio, String lugarSalida,
                                int vehiculoId, Usuario usuario) throws Exception {

        Viaje viaje;
        Vehiculo vehiculo;
        try {
            viaje = viajeDAO.getByViaje(idViaje);
        } catch (DataAccessException e) {
            throw new Exception("No se pudo registrar el viaje. Intente más tarde.");
        }

        if (viaje == null) {
            throw new Exception("El viaje no existe");
        }

        if (!"admin".equals(usuario.getNombreRol()) &&
                viaje.getConductor().getIdUsuario() != usuario.getIdUsuario()) {
            throw new Exception("No tiene permisos para modificar este viaje");
        }

        if (viaje.isCancelado()) {
            throw new Exception("No se puede modificar un viaje cancelado");
        }

        LocalDate fechaViaje = viaje.getFecha().toLocalDate();
        if (fechaViaje.isBefore(LocalDate.now())) {
            throw new Exception("No se puede modificar un viaje que ya pasó");
        }

        try {
            vehiculo = vehiculoDAO.getById_vehiculo(vehiculoId);
        } catch (DataAccessException e) {
            throw new Exception("No se pudo obtener el vehículo. Intente más tarde.");
        }

        if (vehiculo == null) {
            throw new Exception("El vehículo seleccionado no existe");
        }

        if (vehiculo.getUsuario_duenio_id() != usuario.getIdUsuario() &&
                !"admin".equals(usuario.getNombreRol())) {
            throw new Exception("El vehículo seleccionado no le pertenece");
        }

        int disponibles = viaje.getLugares_disponibles();
        if (lugares > disponibles) {
            throw new Exception("Hay solo" + disponibles + " lugares disponibles. ");
        }

        viaje.setFecha(fecha);
        viaje.setLugares_disponibles(lugares);
        viaje.setOrigen(origen);
        viaje.setDestino(destino);
        viaje.setPrecio_unitario(precio);
        viaje.setLugar_salida(lugarSalida);
        viaje.setVehiculo(vehiculo);

        try {
            viajeDAO.update(viaje, idViaje);
        } catch (DataAccessException e) {
            throw new Exception("No se pudo actualizar el viaje. Intente más tarde.");
        }
    }

    public void eliminarViaje(int idViaje, Usuario usuario) throws Exception {

        Viaje viaje = viajeDAO.getByViaje(idViaje);
        if (viaje == null) {
            throw new Exception("El viaje no existe");
        }

        if (!"admin".equals(usuario.getNombreRol()) &&
                viaje.getConductor().getIdUsuario() != usuario.getIdUsuario()) {
            throw new Exception("No tiene permisos para eliminar este viaje");
        }

        try {
            viajeDAO.delete(viaje);
        } catch (DataAccessException e) {
            throw new Exception("No se pudo eliminar el viaje. Intente más tarde.");
        }
    }

    public Viaje cancelarViaje(int idViaje, Usuario usuario) throws Exception {


        Viaje viaje = viajeDAO.getByViaje(idViaje);
        if (viaje == null) {
            throw new Exception("El viaje no existe");
        }

        if (!"admin".equals(usuario.getNombreRol()) &&
                viaje.getConductor().getIdUsuario() != usuario.getIdUsuario()) {
            throw new Exception("No tiene permisos para cancelar este viaje");
        }

        if (viaje.isCancelado()) {
            throw new Exception("El viaje ya está cancelado");
        }

        LocalDate fechaViaje = viaje.getFecha().toLocalDate();
        if (fechaViaje.isBefore(LocalDate.now())) {
            throw new Exception("No se puede cancelar un viaje que ya pasó");
        }

        boolean cancelado = viajeDAO.cancelarViaje(idViaje);
        if (!cancelado) {
            throw new DataAccessException("El viaje no existe o no se pudo cancelar");
        }
        return viaje;

    }

    public void crearViaje(Date fecha, int lugares, String origen, String destino,
                           double precio, String lugarSalida, int vehiculoId,
                           Usuario conductor) throws Exception {

        Vehiculo vehiculo = vehiculoDAO.getById_vehiculo(vehiculoId);
        if (vehiculo == null) {
            throw new Exception("El vehículo seleccionado no existe");
        }

        if (vehiculo.getUsuario_duenio_id() != conductor.getIdUsuario()) {
            throw new Exception("El vehículo seleccionado no le pertenece");
        }

        LocalDate fechaViaje = fecha.toLocalDate();
        if (fechaViaje.isBefore(LocalDate.now())) {
            throw new Exception("La fecha del viaje no puede ser en el pasado");
        }

        Viaje viaje = new Viaje();
        viaje.setFecha(fecha);
        viaje.setLugares_disponibles(lugares);
        viaje.setOrigen(origen);
        viaje.setDestino(destino);
        viaje.setPrecio_unitario(precio);
        viaje.setLugar_salida(lugarSalida);
        viaje.setConductor(conductor);
        viaje.setVehiculo(vehiculo);
        viaje.setCancelado(false);

        try {
            viajeDAO.add(viaje);
        } catch (DataAccessException e) {
            throw new Exception("No se pudo registrar el viaje. Intente más tarde.");
        }
    }
}