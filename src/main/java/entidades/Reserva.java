package entidades;


public class Reserva {
    private int idReserva;
    private String fecha_reserva;
    private int cantidad_pasajeros_reservada;
    private boolean reserva_cancelada;
    private int id_pasajero_reserva;
    private String estado;
    private int intentos_codigo;
    private Viaje viaje;

    public int getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(int idReserva) {
        this.idReserva = idReserva;
    }

    public String getFecha_reserva() {
        return fecha_reserva;
    }

    public void setFecha_reserva(String fecha_reserva) {
        this.fecha_reserva = fecha_reserva;
    }

    public int getCantidad_pasajeros_reservada() {
        return cantidad_pasajeros_reservada;
    }

    public void setCantidad_pasajeros_reservada(int cantidad_pasajeros_reservada) {
        this.cantidad_pasajeros_reservada = cantidad_pasajeros_reservada;
    }

    public boolean isReserva_cancelada() {
        return reserva_cancelada;
    }

    public void setReserva_cancelada(boolean reserva_cancelada) {
        this.reserva_cancelada = reserva_cancelada;
    }

    public int getId_pasajero_reserva() {
        return id_pasajero_reserva;
    }

    public void setId_pasajero_reserva(int id_pasajero_reserva) {
        this.id_pasajero_reserva = id_pasajero_reserva;
    }

    public Viaje getViaje() {
        return viaje;
    }

    public void setViaje(Viaje viaje) {
        this.viaje = viaje;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getIntentos_codigo() {
        return intentos_codigo;
    }

    public void setIntentos_codigo(int intentos_codigo) {
        this.intentos_codigo = intentos_codigo;
    }

    public Reserva(String fecha, int cantidad_pasajeros_reservada, boolean reserva_cancelada, Viaje viaje,
                   int id_pasajero_reserva, String estado, int intentos_codigo) {
        super();
        this.fecha_reserva = fecha;
        this.cantidad_pasajeros_reservada = cantidad_pasajeros_reservada;
        this.reserva_cancelada = reserva_cancelada;
        this.viaje = viaje;
        this.id_pasajero_reserva = id_pasajero_reserva;
        this.estado = estado;
        this.intentos_codigo = intentos_codigo;
    }

    public Reserva() {
        // TODO Auto-generated constructor stub
    }


}