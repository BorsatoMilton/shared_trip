package utils;

import entidades.Usuario;
import entidades.Viaje;

public class Formatters {
    
    // Constructor vacío
    public Formatters() {
        // Constructor por defecto
    }
    
    /**
     * Formatea los datos de un viaje para emails y notificaciones
     */
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

    /**
     * Formatea los datos de un chofer para emails y notificaciones
     */
    public String formatDatosChofer(Usuario chofer, String patente) {
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

    /**
     * Formatea los datos de un pasajero para emails y notificaciones
     */
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