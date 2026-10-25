package umg.modelo;

public class Paquete {

    private int idPaquete;
    private String descripcion;
    private String destinatario;
    private String direccionEnvio;
    private int idCamionero;
    private int idCiudad;

    public Paquete() {
    }

    public Paquete(int idPaquete, String descripcion,
                   String destinatario, String direccionEnvio,
                   int idCamionero, int idCiudad) {
        this.idPaquete = idPaquete;
        this.descripcion = descripcion;
        this.destinatario = destinatario;
        this.direccionEnvio = direccionEnvio;
        this.idCamionero = idCamionero;
        this.idCiudad = idCiudad;
    }

    public int getIdPaquete() {
        return idPaquete;
    }

    public void setIdPaquete(int idPaquete) {
        this.idPaquete = idPaquete;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    public String getDireccionEnvio() {
        return direccionEnvio;
    }

    public void setDireccionEnvio(String direccionEnvio) {
        this.direccionEnvio = direccionEnvio;
    }

    public int getIdCamionero() {
        return idCamionero;
    }

    public void setIdCamionero(int idCamionero) {
        this.idCamionero = idCamionero;
    }

    public int getIdCiudad() {
        return idCiudad;
    }

    public void setIdCiudad(int idCiudad) {
        this.idCiudad = idCiudad;
    }

    @Override
    public String toString() {
        return idPaquete + " | " + descripcion + " | "
            + destinatario + " | " + direccionEnvio
            + " | " + idCamionero + " | " + idCiudad;
    }
}