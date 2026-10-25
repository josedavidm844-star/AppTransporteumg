package umg.modelo;

public class Camionero {

    private int idCamionero;
    private String nombre;
    private String telefono;
    private String direccion;
    private double salario;
    private String poblacion;

    public Camionero() {
    }

    public Camionero(int idCamionero, String nombre, String telefono,
                     String direccion, double salario, String poblacion) {
        this.idCamionero = idCamionero;
        this.nombre = nombre;
        this.telefono = telefono;
        this.direccion = direccion;
        this.salario = salario;
        this.poblacion = poblacion;
    }

    public int getIdCamionero() {
        return idCamionero;
    }

    public void setIdCamionero(int idCamionero) {
        this.idCamionero = idCamionero;
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

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public double getSalario() {
        return salario;
    }

    public void setSalario(double salario) {
        this.salario = salario;
    }

    public String getPoblacion() {
        return poblacion;
    }

    public void setPoblacion(String poblacion) {
        this.poblacion = poblacion;
    }

    @Override
    public String toString() {
        return idCamionero + " | " + nombre
            + " | " + (telefono == null ? "" : telefono)
            + " | " + (direccion == null ? "" : direccion)
            + " | " + salario
            + " | " + (poblacion == null ? "" : poblacion);
    }
}