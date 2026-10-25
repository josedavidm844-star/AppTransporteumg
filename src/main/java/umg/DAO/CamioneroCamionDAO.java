package umg.DAO;

import umg.modelo.CamioneroCamion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class CamioneroCamionDAO {

    private void validarId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException(
                "El ID debe ser mayor que cero."
            );
        }
    }

    private void validar(CamioneroCamion asignacion) {
        if (asignacion == null) {
            throw new IllegalArgumentException(
                "Debe indicar una asignacion."
            );
        }

        validarId(asignacion.getIdCamionero());

        if (asignacion.getIdCamion() != null) {
            validarId(asignacion.getIdCamion());
        }
    }

    private boolean existe(Connection cn, String sql, int id)
            throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private void validarReferencias(
            Connection cn, CamioneroCamion asignacion)
            throws SQLException {

        if (!existe(cn,
                "SELECT 1 FROM camionero WHERE idCamionero = ?",
                asignacion.getIdCamionero())) {
            throw new IllegalArgumentException(
                "El camionero seleccionado no existe."
            );
        }

        if (asignacion.getIdCamion() != null
                && !existe(cn,
                    "SELECT 1 FROM camion WHERE idCamion = ?",
                    asignacion.getIdCamion())) {
            throw new IllegalArgumentException(
                "El camion seleccionado no existe."
            );
        }
    }

    private void verificarDuplicado(
            Connection cn, CamioneroCamion asignacion, int excluirId)
            throws SQLException {

        String sql = """
            SELECT 1
            FROM camionerocamion
            WHERE idCamionero = ?
              AND idCamion <=> ?
              AND idCamioneroCamion <> ?
            """;

        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            asignarDatos(ps, asignacion);
            ps.setInt(3, excluirId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    throw new IllegalArgumentException(
                        "Esa asignacion ya esta registrada."
                    );
                }
            }
        }
    }

    private void asignarDatos(
            PreparedStatement ps, CamioneroCamion asignacion)
            throws SQLException {

        ps.setInt(1, asignacion.getIdCamionero());

        if (asignacion.getIdCamion() == null) {
            ps.setNull(2, Types.INTEGER);
        } else {
            ps.setInt(2, asignacion.getIdCamion());
        }
    }

    private CamioneroCamion leerFila(ResultSet rs)
            throws SQLException {

        int valor = rs.getInt("idCamion");
        Integer idCamion = rs.wasNull()
            ? null : Integer.valueOf(valor);

        return new CamioneroCamion(
            rs.getInt("idCamioneroCamion"),
            rs.getInt("idCamionero"),
            idCamion
        );
    }

    public void insertar(CamioneroCamion asignacion)
            throws SQLException {

        validar(asignacion);

        String sql = """
            INSERT INTO camionerocamion (idCamionero, idCamion)
            VALUES (?, ?)
            """;

        try (Connection cn = ConexionDB.abrir()) {
            validarReferencias(cn, asignacion);
            verificarDuplicado(cn, asignacion, 0);

            try (PreparedStatement ps = cn.prepareStatement(sql)) {
                asignarDatos(ps, asignacion);

                if (ps.executeUpdate() != 1) {
                    throw new SQLException(
                        "No se pudo guardar la asignacion."
                    );
                }
            }
        }
    }

    public List<CamioneroCamion> listar() throws SQLException {
        List<CamioneroCamion> asignaciones = new ArrayList<>();

        String sql = """
            SELECT idCamioneroCamion, idCamionero, idCamion
            FROM camionerocamion
            ORDER BY idCamioneroCamion
            """;

        try (Connection cn = ConexionDB.abrir();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                asignaciones.add(leerFila(rs));
            }
        }

        return asignaciones;
    }

    public CamioneroCamion buscarPorId(int id)
            throws SQLException {

        validarId(id);

        String sql = """
            SELECT idCamioneroCamion, idCamionero, idCamion
            FROM camionerocamion
            WHERE idCamioneroCamion = ?
            """;

        try (Connection cn = ConexionDB.abrir();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? leerFila(rs) : null;
            }
        }
    }

    public boolean actualizar(CamioneroCamion asignacion)
            throws SQLException {

        validar(asignacion);
        validarId(asignacion.getIdCamioneroCamion());

        String sql = """
            UPDATE camionerocamion
            SET idCamionero = ?, idCamion = ?
            WHERE idCamioneroCamion = ?
            """;

        try (Connection cn = ConexionDB.abrir()) {
            validarReferencias(cn, asignacion);

            verificarDuplicado(
                cn, asignacion, asignacion.getIdCamioneroCamion()
            );

            try (PreparedStatement ps = cn.prepareStatement(sql)) {
                asignarDatos(ps, asignacion);
                ps.setInt(3, asignacion.getIdCamioneroCamion());

                return ps.executeUpdate() > 0;
            }
        }
    }

    public boolean eliminar(int id) throws SQLException {
        validarId(id);

        String sql = """
            DELETE FROM camionerocamion
            WHERE idCamioneroCamion = ?
            """;

        try (Connection cn = ConexionDB.abrir();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
}