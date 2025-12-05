package utils;

import entities.Usuario;
import entities.Vehiculo;
import entities.Viaje;

public class Formatters {
    

    public Formatters() {}
    

    public String formatDatosViaje(Viaje viaje) {
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

    public String formatDatosChofer(Usuario chofer,  Vehiculo vehiculo) {
        if (chofer == null) {
            return "Información del chofer no disponible";
        }

        String nombreCompleto = (chofer.getNombre() != null ? chofer.getNombre() : "No especificado")
                + (chofer.getApellido() != null ? " " + chofer.getApellido() : "");
        String telefono = chofer.getTelefono() != null ? chofer.getTelefono() : "No especificado";
        String correo = chofer.getCorreo() != null ? chofer.getCorreo() : "No especificado";
        String vehiculoInfo = "No especificado";
        if (vehiculo != null && vehiculo.getPatente() != null) {
            vehiculoInfo = vehiculo.getPatente();
            if (vehiculo.getModelo() != null) {
                vehiculoInfo += " (" + vehiculo.getModelo() + ")";
            }
        }

        return String.format(
                "Nombre: %s<br>Teléfono: %s<br>Email: %s<br>Vehículo: %s",
                nombreCompleto, telefono, correo, vehiculoInfo
        );
    }

    public String formatDatosPasajero(Usuario pasajero, int cantPasajeros) {
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