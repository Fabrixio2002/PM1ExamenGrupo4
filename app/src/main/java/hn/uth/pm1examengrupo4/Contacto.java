package hn.uth.pm1examengrupo4;
public class Contacto {
    private String imagen;
    private String nombre;
    private String telefono;
    private String latitud;
    private String longitud;

    public Contacto() {
        // Constructor vacío requerido para Firebase
    }

    public Contacto(String imagen, String nombre, String telefono, String latitud, String longitud) {
        this.imagen = imagen;
        this.nombre = nombre;
        this.telefono = telefono;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }
}
