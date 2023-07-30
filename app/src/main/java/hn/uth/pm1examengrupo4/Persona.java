package hn.uth.pm1examengrupo4;

public class Persona {
    // Atributos
    private String imagen;
    private String nombre;
    private String apellido;
    private String fechaNacimiento;
    private String genero;
    // Constructor vacío
    public Persona() {
    }

    // Constructor con parámetros
    public Persona(String imagen, String nombre, String apellido, String fechaNacimiento,String genero) {
        this.imagen = imagen;
        this.nombre = nombre;
        this.apellido = apellido;
        this.fechaNacimiento = fechaNacimiento;
        this.genero=genero;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }
// Métodos getter y setter para cada atributo

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

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }
}
