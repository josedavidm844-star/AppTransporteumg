package umg.test;

import umg.DAO.ConexionDB;
import umg.DAO.CiudadDAO;
import umg.modelo.Ciudad;
import java.sql.Connection;
import java.util.List;

public class TestConexion {
    public static void main(String[] args) {
        System.out.println("--- Probando conexion con MySQL Workbench ---");
        try (Connection cn = ConexionDB.abrir()) {
            if (cn != null && !cn.isClosed()) {
                System.out.println("[OK] Conexion exitosa a la base de datos db_paqueteria!");
                System.out.println("Catalogo actual: " + cn.getCatalog());
            }

            CiudadDAO ciudadDAO = new CiudadDAO();
            List<Ciudad> ciudades = ciudadDAO.listar();
            System.out.println("[OK] Consulta a la tabla ciudad realizada correctamente.");
            System.out.println("Total de ciudades encontradas: " + ciudades.size());
            for (Ciudad c : ciudades) {
                System.out.println(" - ID: " + c.getIdCiudad() + " | Nombre: " + c.getNombre());
            }
            System.out.println("--- PRUEBA COMPLETADA CON EXITO ---");
        } catch (Exception e) {
            System.err.println("[ERROR] Error en la conexion: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
