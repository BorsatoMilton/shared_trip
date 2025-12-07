package entities;


import java.util.Date;

public class PasswordReset {
    private Usuario usuario;
    private String token;
    private boolean utilizado;
    private int id_password_reset;
    private Date fecha_creacion;

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isUtilizado() {
        return utilizado;
    }

    public void setUtilizado(boolean utilizado) {
        this.utilizado = utilizado;
    }

    public int getId_password_reset() {
        return id_password_reset;
    }

    public void setId_password_reset(int id_password_reset) {
        this.id_password_reset = id_password_reset;
    }

    public Date getFecha_creacion() {
        return fecha_creacion;
    }

    public void setFecha_hora_creacion(Date fecha_creacion) {
        this.fecha_creacion = fecha_creacion;
    }
}
