package umg.UI;

import umg.DAO.CiudadDAO;
import umg.modelo.Ciudad;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class PruebaCiudad {

    private static final Scanner entrada = Entrada.TECLADO;
    private static final CiudadDAO dao = new CiudadDAO();

    public static void main(String[] args) {
        boolean continuar = true;

        while (continuar) {
            System.out.println();
            System.out.println("===== CIUDADES =====");
            System.out.println("1. Agregar");
            System.out.println("2. Listar");
            System.out.println("3. Modificar");
            System.out.println("4. Eliminar");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opcion: ");

            String opcion = entrada.nextLine().trim();

            try {
                switch (opcion) {
                    case "1" -> agregar();
                    case "2" -> listar();
                    case "3" -> modificar();
                    case "4" -> eliminar();
                    case "0" -> continuar = false;
                    default -> System.out.println("Opcion incorrecta.");
                }

            } catch (NumberFormatException e) {
                System.out.println("El ID debe ser un numero entero valido.");

            } catch (IllegalArgumentException e) {
                System.out.println("Dato incorrecto: " + e.getMessage());

            } catch (SQLException e) {
                if (e.getErrorCode() == 1451) {
                    System.out.println(
                        "No se puede eliminar: la ciudad tiene paquetes asociados."
                    );
                } else {
                    System.out.println(
                        "Error de base de datos: " + e.getMessage()
                    );
                }
            }
        }

        System.out.println("Menu de ciudades finalizado.");
    }

    private static void agregar() throws SQLException {
        System.out.print("Nombre de la ciudad: ");
        String nombre = entrada.nextLine();

        Ciudad ciudad = new Ciudad(0, nombre);
        dao.insertar(ciudad);
        System.out.println("Ciudad guardada correctamente.");
    }

    private static void listar() throws SQLException {
        List<Ciudad> ciudades = dao.listar();

        if (ciudades.isEmpty()) {
            System.out.println("No hay ciudades registradas.");
            return;
        }

        System.out.println();
        System.out.println("ID | NOMBRE");
        for (Ciudad c : ciudades) {
            System.out.println(c);
        }
    }

    private static void modificar() throws SQLException {
        listar();

        System.out.print("ID de la ciudad que desea modificar: ");
        int id = Integer.parseInt(entrada.nextLine().trim());

        Ciudad actual = dao.buscarPorId(id);
        if (actual == null) {
            System.out.println("No existe una ciudad con ese ID.");
            return;
        }

        System.out.println("Nombre actual: " + actual.getNombre());
        System.out.print("Nuevo nombre: ");
        String nuevoNombre = entrada.nextLine();

        actual.setNombre(nuevoNombre);
        if (dao.actualizar(actual)) {
            System.out.println("Ciudad actualizada correctamente.");
        } else {
            System.out.println("No se reportaron cambios.");
        }
    }

    private static void eliminar() throws SQLException {
        listar();

        System.out.print("ID de la ciudad que desea eliminar: ");
        int id = Integer.parseInt(entrada.nextLine().trim());

        Ciudad actual = dao.buscarPorId(id);
        if (actual == null) {
            System.out.println("No existe una ciudad con ese ID.");
            return;
        }

        System.out.println("Ciudad seleccionada: " + actual);
        System.out.print("Para confirmar la eliminacion escriba SI: ");

        if (!entrada.nextLine().trim().equalsIgnoreCase("SI")) {
            System.out.println("Eliminacion cancelada.");
            return;
        }

        if (dao.eliminar(id)) {
            System.out.println("Ciudad eliminada correctamente.");
        } else {
            System.out.println("No se elimino ningun registro.");
        }
    }
}
