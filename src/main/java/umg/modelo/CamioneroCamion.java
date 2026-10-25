package umg.modelo;

public class CamioneroCamion {

    private int idCamioneroCamion;
    private int idCamionero;
    private Integer idCamion;

    public CamioneroCamion() {
    }

    public CamioneroCamion(int idCamioneroCamion,
                          int idCamionero, Integer idCamion) {
        this.idCamioneroCamion = idCamioneroCamion;
        this.idCamionero = idCamionero;
        this.idCamion = idCamion;
    }

    public int getIdCamioneroCamion() {
        return idCamioneroCamion;
    }

    public void setIdCamioneroCamion(int idCamioneroCamion) {
        this.idCamioneroCamion = idCamioneroCamion;
    }

    public int getIdCamionero() {
        return idCamionero;
    }

    public void setIdCamionero(int idCamionero) {
        this.idCamionero = idCamionero;
    }

    public Integer getIdCamion() {
        return idCamion;
    }

    public void setIdCamion(Integer idCamion) {
        this.idCamion = idCamion;
    }

    @Override
    public String toString() {
        return idCamioneroCamion + " | " + idCamionero
            + " | " + (idCamion == null ? "Sin camion" : idCamion);
    }
}