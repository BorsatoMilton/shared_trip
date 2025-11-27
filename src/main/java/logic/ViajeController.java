package logic;

import java.sql.Date;
import java.time.LocalDate;
import java.util.LinkedList;

import data.*;
import entidades.*;

public class ViajeController {
    private final ViajeDAO viajeDAO;
    private final VehiculoDAO vehiculoDAO;

    public ViajeController() {
        this.viajeDAO = new ViajeDAO();
        this.vehiculoDAO = new VehiculoDAO();
    }

    public LinkedList<Viaje> getAll() {
        return viajeDAO.getAll();
    }

    public LinkedList<Viaje> getAllBySearch(String origen, String destino, String fecha) {
        LinkedList<Viaje> viajes = viajeDAO.getAllBySearch(origen, destino, fecha);
        return viajes;
    }

    public Viaje getOne(int id) {
        return viajeDAO.getByViaje(id);
    }

    public LinkedList<Viaje> getViajesUsuario(Usuario u) {
        return viajeDAO.getByUser(u);

    }

    public void actualizarCantidad(int idViaje, int cantidad) {
        Viaje viaje = this.getOne(idViaje);
        int nueva_cant = viaje.getLugares_disponibles() - (cantidad);
        viajeDAO.updateCantidad(idViaje, nueva_cant);
    }

    public void actualizarViaje(int idViaje, Date fecha, int lugares, String origen,
                                String destino, double precio, String lugarSalida,
                                int vehiculoId, Usuario usuario) throws Exception {

        Viaje viaje = viajeDAO.getByViaje(idViaje);
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

        Vehiculo vehiculo = vehiculoDAO.getById_vehiculo(vehiculoId);
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

        // 7. Actualizar el viaje
        viaje.setFecha(fecha);
        viaje.setLugares_disponibles(lugares);
        viaje.setOrigen(origen);
        viaje.setDestino(destino);
        viaje.setPrecio_unitario(precio);
        viaje.setLugar_salida(lugarSalida);
        viaje.setVehiculo(vehiculo);

        viajeDAO.update(viaje, idViaje);
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

        viajeDAO.delete(viaje);
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
            throw new Exception("Error al cancelar el viaje en la base de datos");
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


        viajeDAO.add(viaje);

    }
}