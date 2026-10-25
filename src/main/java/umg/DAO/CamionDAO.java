package umg.DAO;

import umg.modelo.Camion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class CamionDAO {

    private String textoObligatorio(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException(
                campo + " es obligatorio."
            );
        }

        String texto = valor.trim();

        if (texto.length() > 45) {
            throw new IllegalArgumentException(
                campo + " admite hasta 45 caracteres."
            );
        }

        return texto;
    }

    private void validar(Camion camion) {
        if (camion == null) {
            throw new IllegalArgumentException("Debe indicar un camion.");
        }

        camion.setNoPlaca(
            textoObligatorio(camion.getNoPlaca(), "La placa")
                .toUpperCase(java.util.Locale.ROOT)
        );

        camion.setModelo(
            textoObligatorio(camion.getModelo(), "El modelo")
        );

        String tipo = camion.getTipo();

        if (tipo != null) {
            tipo = tipo.trim();

            if (tipo.length() > 45) {
                throw new IllegalArgumentException(
                    "El tipo admite hasta 45 caracteres."
                );
            }

            camion.setTipo(tipo.isEmpty() ? null : tipo);
        }

        if (camion.getPotencia() != null
                && camion.getPotencia() <= 0) {
            throw new IllegalArgumentException(
                "La potencia debe ser mayor que cero."
            );
        }
    }

    private void validarId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException(
                "El ID debe ser mayor que cero."
            );
        }
    }

    private void verificarPlaca(Connection cn, Camion camion)
            throws SQLException {

        String sql = """
            SELECT idCamion
            FROM camion
            WHERE NoPlaca = ? AND idCamion <> ?
            """;

        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, camion.getNoPlaca());
            ps.setInt(2, camion.getIdCamion());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    throw new IllegalArgumentException(
                        "Ya existe otro camion con esa placa."
                    );
                }
            }
        }
    }

    private void asignarDatos(PreparedStatement ps, Camion camion)
            throws SQLException {

        ps.setString(1, camion.getNoPlaca());
        ps.setString(2, camion.getModelo());
        ps.setString(3, camion.getTipo());

        if (camion.getPotencia() == null) {
            ps.setNull(4, Types.INTEGER);
        } else {
            ps.setInt(4, camion.getPotencia());
        }
    }

    public void insertar(Camion camion) throws SQLException {
        validar(camion);

        // El ID lo genera MySQL.
        camion.setIdCamion(0);

        String sql = """
            INSERT INTO camion (NoPlaca, Modelo, Tipo, Potencia)
            VALUES (?, ?, ?, ?)
            """;

        try (Connection cn = ConexionDB.abrir()) {
            verificarPlaca(cn, camion);

            try (PreparedStatement ps = cn.prepareStatement(sql)) {
                asignarDatos(ps, camion);

                if (ps.executeUpdate() != 1) {
                    throw new SQLException(
                        "No se pudo guardar el camion."
                    );
                }
            }
        }
    }

    public List<Camion> listar() throws SQLException {
        List<Camion> camiones = new ArrayList<>();

        String sql = """
            SELECT idCamion, NoPlaca, Modelo, Tipo, Potencia
            FROM camion
            ORDER BY idCamion
            """;

        try (Connection cn = ConexionDB.abrir();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int valorPotencia = rs.getInt("Potencia");
                Integer potencia =
                    rs.wasNull() ? null : Integer.valueOf(valorPotencia);

                camiones.add(new Camion(
                    rs.getInt("idCamion"),
                    rs.getString("NoPlaca"),
                    rs.getString("Modelo"),
                    rs.getString("Tipo"),
                    potencia
                ));
            }
        }

        return camiones;
    }

    public Camion buscarPorId(int id) throws SQLException {
        validarId(id);

        String sql = """
            SELECT idCamion, NoPlaca, Modelo, Tipo, Potencia
            FROM camion
            WHERE idCamion = ?
            """;

        try (Connection cn = ConexionDB.abrir();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int valorPotencia = rs.getInt("Potencia");
                    Integer potencia = rs.wasNull() ? null : Integer.valueOf(valorPotencia);

                    return new Camion(
                        rs.getInt("idCamion"),
                        rs.getString("NoPlaca"),
                        rs.getString("Modelo"),
                        rs.getString("Tipo"),
                        potencia
                    );
                }
            }
        }

        return null;
    }

    public boolean actualizar(Camion camion) throws SQLException {
        validar(camion);
        validarId(camion.getIdCamion());

        String sql = """
            UPDATE camion
            SET NoPlaca = ?, Modelo = ?, Tipo = ?, Potencia = ?
            WHERE idCamion = ?
            """;

        try (Connection cn = ConexionDB.abrir()) {
            verificarPlaca(cn, camion);

            try (PreparedStatement ps = cn.prepareStatement(sql)) {
                asignarDatos(ps, camion);
                ps.setInt(5, camion.getIdCamion());

                return ps.executeUpdate() > 0;
            }
        }
    }

    public boolean eliminar(int idCamion) throws SQLException {
        validarId(idCamion);

        String sql = "DELETE FROM camion WHERE idCamion = ?";

        try (Connection cn = ConexionDB.abrir();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idCamion);

            return ps.executeUpdate() > 0;
        }
    }
}