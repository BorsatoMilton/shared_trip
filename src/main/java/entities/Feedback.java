package entities;

import java.sql.Date;

public class Feedback {
    private int puntuacion;
    private Date fecha_hora;
    private Usuario usuario_calificado;
    private Reserva reserva;

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
    

    public Feedback(Usuario usuario_calificado, Reserva reserva) {
        super();
        this.usuario_calificado = usuario_calificado;
        this.reserva = reserva;
    }

    public Feedback() {
        // TODO Auto-generated constructor stub
    }

}