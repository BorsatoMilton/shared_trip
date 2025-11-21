package entidades;

import java.sql.Date;

public class Feedback {
    public int puntuacion;
    public String observacion;
    public Date fecha_hora;
    public Usuario usuario_calificado;
    public Reserva reserva;

    public Reserva getReserva() {
        return reserva;
    }

    public void setReserva(Reserva reserva) {
        this.reserva = reserva;
    }

    public int getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(int puntuacion) {
        this.puntuacion = puntuacion;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public Date getFecha_hora() {
        return fecha_hora;
    }

    public void setFecha_hora(Date fecha_hora) {
        this.fecha_hora = fecha_hora;
    }

    public Usuario getUsuario_calificado() {
        return usuario_calificado;
    }

    public void setUsuario_calificado(Usuario usuario_calificado) {
        this.usuario_calificado = usuario_calificado;
    }

    //CHEQUEAR SI SE NECESITA ESTE CONSTRUCTOR
    public Feedback(int puntuacion, String observacion, Date fecha_hora, Usuario usuario_calificado, Reserva reserva) {
        super();
        this.puntuacion = puntuacion;
        this.observacion = observacion;
        this.fecha_hora = fecha_hora;
        this.usuario_calificado = usuario_calificado;
        this.reserva = reserva;
    }

    public Feedback(Usuario usuario_calificado, Reserva reserva) {
        super();
        this.usuario_calificado = usuario_calificado;
        this.reserva = reserva;
    }

    public Feedback() {
        // TODO Auto-generated constructor stub
    }

}