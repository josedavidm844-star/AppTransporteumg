package umg.DAO;

import umg.modelo.Paquete;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PaqueteDAO {

    private String validarTexto(String valor, String campo) {
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

    private void validarId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException(
                "Los ID deben ser mayores que cero."
            );
        }
    }

    private void validar(Paquete p) {
        if (p == null) {
            throw new IllegalArgumentException(
                "Debe indicar un paquete."
            );
        }

        p.setDescripcion(
            validarTexto(p.getDescripcion(), "Descripcion")
        );
        p.setDestinatario(
            validarTexto(p.getDestinatario(), "Destinatario")
        );
        p.setDireccionEnvio(
            validarTexto(p.getDireccionEnvio(), "Direccion")
        );

        validarId(p.getIdCamionero());
        validarId(p.getIdCiudad());
    }

    private boolean existeReferencia(Connection cn, String sql, int id)
            throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private void validarReferencias(Connection cn, Paquete p)
            throws SQLException {

        if (!existeReferencia(cn,
                "SELECT 1 FROM camionero WHERE idCamionero = ?",
                p.getIdCamionero())) {
            throw new IllegalArgumentException(
                "El camionero seleccionado no existe."
            );
        }

        if (!existeReferencia(cn,
                "SELECT 1 FROM ciudad WHERE idCiudad = ?",
                p.getIdCiudad())) {
            throw new IllegalArgumentException(
                "La ciudad seleccionada no existe."
            );
        }
    }

    private void asignarDatos(PreparedStatement ps, Paquete p)
            throws SQLException {
        ps.setString(1, p.getDescripcion());
        ps.setString(2, p.getDestinatario());
        ps.setString(3, p.getDireccionEnvio());
        ps.setInt(4, p.getIdCamionero());
        ps.setInt(5, p.getIdCiudad());
    }

    private Paquete leerFila(ResultSet rs) throws SQLException {
        return new Paquete(
            rs.getInt("idPaquete"),
            rs.getString("Descripcion"),
            rs.getString("Destinatario"),
            rs.getString("DireccionEnvio"),
            rs.getInt("idCamionero"),
            rs.getInt("idCiudad")
        );
    }

    public void insertar(Paquete p) throws SQLException {
        validar(p);

        String sql = """
            INSERT INTO paquete
                (Descripcion, Destinatario, DireccionEnvio,
                 idCamionero, idCiudad)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (Connection cn = ConexionDB.abrir()) {
            validarReferencias(cn, p);

            try (PreparedStatement ps = cn.prepareStatement(sql)) {
                asignarDatos(ps, p);

                if (ps.executeUpdate() != 1) {
                    throw new SQLException(
                        "No se pudo guardar el paquete."
                    );
                }
            }
        }
    }

    public List<Paquete> listar() throws SQLException {
        List<Paquete> paquetes = new ArrayList<>();

        String sql = """
            SELECT idPaquete, Descripcion, Destinatario,
                   DireccionEnvio, idCamionero, idCiudad
            FROM paquete
            ORDER BY idPaquete
            """;

        try (Connection cn = ConexionDB.abrir();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                paquetes.add(leerFila(rs));
            }
        }

        return paquetes;
    }

    public Paquete buscarPorId(int id) throws SQLException {
        validarId(id);

        String sql = """
            SELECT idPaquete, Descripcion, Destinatario,
                   DireccionEnvio, idCamionero, idCiudad
            FROM paquete
            WHERE idPaquete = ?
            """;

        try (Connection cn = ConexionDB.abrir();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? leerFila(rs) : null;
            }
        }
    }

    public boolean actualizar(Paquete p) throws SQLException {
        validar(p);
        validarId(p.getIdPaquete());

        String sql = """
            UPDATE paquete
            SET Descripcion = ?, Destinatario = ?,
                DireccionEnvio = ?, idCamionero = ?, idCiudad = ?
            WHERE idPaquete = ?
            """;

        try (Connection cn = ConexionDB.abrir()) {
            validarReferencias(cn, p);

            try (PreparedStatement ps = cn.prepareStatement(sql)) {
                asignarDatos(ps, p);
                ps.setInt(6, p.getIdPaquete());

                return ps.executeUpdate() > 0;
            }
        }
    }

    public boolean eliminar(int id) throws SQLException {
        validarId(id);

        String sql = "DELETE FROM paquete WHERE idPaquete = ?";

        try (Connection cn = ConexionDB.abrir();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
}   