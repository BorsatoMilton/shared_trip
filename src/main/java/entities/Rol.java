package entities;

public class Rol {
    private int idRol;
    private String nombre;

    public int getIdRol() {
        return idRol;
    }

    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }

    public String getNombreRol() {
        return nombre;
    }

    public void setNombreRol(String nombre) {
        this.nombre = nombre;
    }

    public Rol(int idRol, String nombre) {
        super();
        this.idRol = idRol;
        this.nombre = nombre;
    }

    public Rol() {
        super();

    }

}