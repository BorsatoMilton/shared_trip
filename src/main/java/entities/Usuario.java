package entities;

public class Usuario {
    private int idUsuario;
    private String usuario;
    private String clave;
    private String nombre;
    private String apellido;
    private String correo;
    private String telefono;
    private Rol rol;
    private double promedio_puntuacion;
    private int cantidad_que_puntuaron;

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public double getPromedio_puntuacion() {
        return promedio_puntuacion;
    }

    public void setPromedio_puntuacion(double promedio_puntuacion) {
        this.promedio_puntuacion = promedio_puntuacion;
    }

    public int getCantidad_que_puntuaron() {
        return cantidad_que_puntuaron;
    }

    public void setCantidad_que_puntuaron(int cantidad_que_puntuaron) {
        this.cantidad_que_puntuaron = cantidad_que_puntuaron;
    }

    public Usuario() {

    }

    public Usuario(int idUsuario, String usuario, String clave, String nombre, String apellido, String correo,
                   String telefono, Rol rol) {
        super();
        this.idUsuario = idUsuario;
        this.usuario = usuario;
        this.clave = clave;
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.telefono = telefono;
        this.rol = rol;
    }
}