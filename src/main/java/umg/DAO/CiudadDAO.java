package umg.DAO;

import umg.modelo.Ciudad;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CiudadDAO {

    private String validarNombre(Ciudad ciudad) {
        if (ciudad == null || ciudad.getNombre() == null) {
            throw new IllegalArgumentException(
                "Debe ingresar el nombre de la ciudad."
            );
        }

        String nombre = ciudad.getNombre().trim();

        if (nombre.isEmpty() || nombre.length() > 45) {
            throw new IllegalArgumentException(
                "El nombre debe tener entre 1 y 45 caracteres."
            );
        }

        return nombre;
    }

    public void insertar(Ciudad ciudad) throws SQLException {
        String nombre = validarNombre(ciudad);
        String sql = "INSERT INTO ciudad (Nombre) VALUES (?)";

        try (Connection cn = ConexionDB.abrir();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, nombre);

            if (ps.executeUpdate() != 1) {
                throw new SQLException("No se pudo insertar la ciudad.");
            }
        }
    }

    public List<Ciudad> listar() throws SQLException {
        List<Ciudad> ciudades = new ArrayList<>();

        String sql =
            "SELECT idCiudad, Nombre FROM ciudad ORDER BY idCiudad";

        try (Connection cn = ConexionDB.abrir();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ciudades.add(new Ciudad(
                    rs.getInt("idCiudad"),
                    rs.getString("Nombre")
                ));
            }
        }

        return ciudades;
    }

    public boolean actualizar(Ciudad ciudad) throws SQLException {
        String nombre = validarNombre(ciudad);

        if (ciudad.getIdCiudad() <= 0) {
            throw new IllegalArgumentException(
                "El ID de la ciudad debe ser mayor que cero."
            );
        }

        String sql =
            "UPDATE ciudad SET Nombre = ? WHERE idCiudad = ?";

        try (Connection cn = ConexionDB.abrir();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, nombre);
            ps.setInt(2, ciudad.getIdCiudad());

            return ps.executeUpdate() > 0;
        }
    }

    public Ciudad buscarPorId(int idCiudad) throws SQLException {
        if (idCiudad <= 0) {
            throw new IllegalArgumentException(
                "El ID de la ciudad debe ser mayor que cero."
            );
        }

        String sql = "SELECT idCiudad, Nombre FROM ciudad WHERE idCiudad = ?";

        try (Connection cn = ConexionDB.abrir();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idCiudad);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Ciudad(
                        rs.getInt("idCiudad"),
                        rs.getString("Nombre")
                    );
                }
            }
        }
        return null;
    }

    public boolean eliminar(int idCiudad) throws SQLException {
        if (idCiudad <= 0) {
            throw new IllegalArgumentException(
                "El ID de la ciudad debe ser mayor que cero."
            );
        }

        String sql = "DELETE FROM ciudad WHERE idCiudad = ?";

        try (Connection cn = ConexionDB.abrir();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idCiudad);

            return ps.executeUpdate() > 0;
        }
    }
}