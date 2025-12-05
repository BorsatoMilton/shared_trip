package logic;

import java.util.LinkedList;

import data.*;
import entities.*;

public class VehiculoController {

    private final VehiculoDAO vehiculoDAO;

    public VehiculoController() {
        this.vehiculoDAO = new VehiculoDAO();
    }

    public LinkedList<Vehiculo> getAll() {
        return vehiculoDAO.getAll();

    }

    public Vehiculo getOne(int id_vehiculo) {
        return this.vehiculoDAO.getById_vehiculo(id_vehiculo);
    }

    public Vehiculo getByPatente(String patente) {
        return this.vehiculoDAO.getByPatente(patente);
    }

    public void actualizarVehiculo(int idVehiculo, String patente, String modelo,
                                   int anio, Usuario usuario) throws Exception {

        Vehiculo v = vehiculoDAO.getById_vehiculo(idVehiculo);
        if (v == null) {
            throw new Exception("El vehículo no existe");
        }

        if (!"admin".equals(usuario.getNombreRol()) &&
                v.getUsuario_duenio_id() != usuario.getIdUsuario()) {
            throw new Exception("No tiene permisos para modificar este vehículo");
        }

        if (!v.getPatente().equals(patente)) {
            Vehiculo existente = vehiculoDAO.getByPatente(patente);
            if (existente != null && existente.getId_vehiculo() != idVehiculo) {
                throw new Exception("La patente " + patente + " ya está registrada en otro vehículo");
            }
        }

        v.setPatente(patente);
        v.setModelo(modelo);
        v.setAnio(anio);

        vehiculoDAO.update(v, idVehiculo);
    }

    public LinkedList<Vehiculo> getVehiculosUsuario(Usuario u) {
        return this.vehiculoDAO.getByUser(u);

    }

    public void eliminarVehiculo(int idVehiculo, Usuario usuario) throws Exception {

        Vehiculo v = vehiculoDAO.getById_vehiculo(idVehiculo);
        if (v == null) {
            throw new Exception("El vehículo no existe");
        }

        if (!"admin".equals(usuario.getNombreRol()) &&
                v.getUsuario_duenio_id() != usuario.getIdUsuario()) {
            throw new Exception("No tiene permisos para eliminar este vehículo");
        }

        if (tieneViajes(idVehiculo)) {
            throw new Exception("No se puede eliminar un vehículo con viajes registrados. ");
        }

        boolean eliminado = vehiculoDAO.eliminarVehiculo(idVehiculo);
        if (!eliminado) {
            throw new Exception("Error al eliminar el vehículo de la base de datos");
        }
    }

    public void crearVehiculo(String patente, String modelo, int anio, int idUsuario)
            throws Exception {

        Vehiculo existente = vehiculoDAO.getByPatente(patente);
        if (existente != null) {
            throw new Exception("La patente " + patente + " ya está registrada");
        }

        Vehiculo v = new Vehiculo();
        v.setPatente(patente);
        v.setModelo(modelo);
        v.setAnio(anio);
        v.setUsuario_duenio_id(idUsuario);

        vehiculoDAO.altaVehiculo(v);
    }

    private boolean tieneViajes(int idVehiculo) {
        return vehiculoDAO.tieneViajes(idVehiculo);
    }
}
