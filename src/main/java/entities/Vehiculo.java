package entities;

public class Vehiculo {
    private int id_vehiculo;
    private String patente;
    private String modelo;
    private int anio;
    private int usuario_duenio_id;
    private Usuario propietario;


    public int getId_vehiculo() {
        return id_vehiculo;
    }

    public void setId_vehiculo(int id_vehiculo) {
        this.id_vehiculo = id_vehiculo;
    }

    public String getPatente() {
        return patente;
    }

    public void setPatente(String patente) {
        this.patente = patente;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public int getUsuario_duenio_id() {
        return usuario_duenio_id;
    }

    public void setUsuario_duenio_id(int usuario_duenio_id) {
        this.usuario_duenio_id = usuario_duenio_id;
    }

    public Usuario getPropietario() {
        return propietario;
    }

    public void setPropietario(Usuario propietario) {
        this.propietario = propietario;
    }


    public Vehiculo() {

    }

    public Vehiculo(int id_vehiculo, String patente, String modelo, int anio, int usuario_duenio_id) {
        super();
        this.id_vehiculo = id_vehiculo;
        this.patente = patente;
        this.modelo = modelo;
        this.anio = anio;
        this.usuario_duenio_id = usuario_duenio_id;

    }

}