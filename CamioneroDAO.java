package umg.DAO;

import umg.modelo.Camionero;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CamioneroDAO {

    private String validarTexto(String valor, String campo,
                                int limite, boolean obligatorio) {
        String texto = valor == null ? "" : valor.trim();

        if (obligatorio && texto.isEmpty()) {
            throw new IllegalArgumentException(
                campo + " es obligatorio."
            );
        }

        if (texto.length() > limite) {
            throw new IllegalArgumentException(
                campo + " admite hasta " + limite + " caracteres."
            );
        }

        return texto.isEmpty() ? null : texto;
    }

    private void validar(Camionero c) {
        if (c == null) {
            throw new IllegalArgumentException(
                "Debe indicar un camionero."
            );
        }

        c.setNombre(validarTexto(
            c.getNombre(), "Nombre", 60, true
        ));

        c.setTelefono(validarTexto(
            c.getTelefono(), "Telefono", 20, false
        ));

        c.setDireccion(validarTexto(
            c.getDireccion(), "Direccion", 100, false
        ));

        c.setPoblacion(validarTexto(
            c.getPoblacion(), "Poblacion", 45, false
        ));

        if (c.getTelefono() != null
                && !c.getTelefono().matches("[0-9+() -]+")) {
            throw new IllegalArgumentException(
                "El telefono solo admite numeros, espacios, +, -, y parentesis."
            );
        }

        if (!Double.isFinite(c.getSalario())
                || c.getSalario() < 0
                || c.getSalario() >= 1.0e16) {
            throw new IllegalArgumentException(
                "El salario debe ser un numero entre 0 y menos de 10^16."
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

    private void asignarDatos(PreparedStatement ps, Camionero c)
            throws SQLException {
        ps.setString(1, c.getNombre());
        ps.setString(2, c.getTelefono());
        ps.setString(3, c.getDireccion());
        ps.setDouble(4, c.getSalario());
        ps.setString(5, c.getPoblacion());
    }

    private Camionero leerFila(ResultSet rs) throws SQLException {
        return new Camionero(
            rs.getInt("idCamionero"),
            rs.getString("Nombre"),
            rs.getString("Telefono"),
            rs.getString("Direccion"),
            rs.getDouble("Salario"),
            rs.getString("Poblacion")
        );
    }

    public void insertar(Camionero c) throws SQLException {
        validar(c);

        String sql = """
            INSERT INTO camionero
                (Nombre, Telefono, Direccion, Salario, Poblacion)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (Connection cn = ConexionDB.abrir();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            asignarDatos(ps, c);

            if (ps.executeUpdate() != 1) {
                throw new SQLException(
                    "No se pudo guardar el camionero."
                );
            }
        }
    }

    public List<Camionero> listar() throws SQLException {
        List<Camionero> camioneros = new ArrayList<>();

        String sql = """
            SELECT idCamionero, Nombre, Telefono,
                   Direccion, Salario, Poblacion
            FROM camionero
            ORDER BY idCamionero
            """;

        try (Connection cn = ConexionDB.abrir();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                camioneros.add(leerFila(rs));
            }
        }

        return camioneros;
    }

    public Camionero buscarPorId(int id) throws SQLException {
        validarId(id);

        String sql = """
            SELECT idCamionero, Nombre, Telefono,
                   Direccion, Salario, Poblacion
            FROM camionero
            WHERE idCamionero = ?
            """;

        try (Connection cn = ConexionDB.abrir();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? leerFila(rs) : null;
            }
        }
    }

    public boolean actualizar(Camionero c) throws SQLException {
        validar(c);
        validarId(c.getIdCamionero());

        String sql = """
            UPDATE camionero
            SET Nombre = ?, Telefono = ?, Direccion = ?,
                Salario = ?, Poblacion = ?
            WHERE idCamionero = ?
            """;

        try (Connection cn = ConexionDB.abrir();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            asignarDatos(ps, c);
            ps.setInt(6, c.getIdCamionero());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminar(int id) throws SQLException {
        validarId(id);

        String sql =
            "DELETE FROM camionero WHERE idCamionero = ?";

        try (Connection cn = ConexionDB.abrir();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);

            return ps.executeUpdate() > 0;
        }
    }
}