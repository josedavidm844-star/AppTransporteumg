package umg.UI;

import umg.DAO.CamioneroDAO;
import umg.modelo.Camionero;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class PruebaCamionero {

    private static final Scanner entrada = Entrada.TECLADO;
    private static final CamioneroDAO dao = new CamioneroDAO();

    public static void main(String[] args) {
        boolean continuar = true;

        while (continuar) {
            System.out.println();
            System.out.println("===== CAMIONEROS =====");
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
                System.out.println(
                    "ID: use numeros enteros. Salario: use punto decimal."
                );

            } catch (IllegalArgumentException e) {
                System.out.println(
                    "Dato incorrecto: " + e.getMessage()
                );

            } catch (SQLException e) {
                if (e.getErrorCode() == 1451) {
                    System.out.println(
                        "No se puede eliminar: el camionero tiene "
                        + "paquetes o asignaciones relacionados."
                    );
                } else {
                    System.out.println(
                        "Error de base de datos: " + e.getMessage()
                    );
                }
            }
        }

        System.out.println("Programa finalizado.");
    }

    private static Camionero leerDatos(int id) {
        System.out.print("Nombre completo: ");
        String nombre = entrada.nextLine();

        System.out.print("Telefono (opcional): ");
        String telefono = entrada.nextLine();

        System.out.print("Direccion (opcional): ");
        String direccion = entrada.nextLine();

        System.out.print("Salario: ");
        double salario =
            Double.parseDouble(entrada.nextLine().trim());

        System.out.print("Poblacion (opcional): ");
        String poblacion = entrada.nextLine();

        return new Camionero(
            id, nombre, telefono, direccion, salario, poblacion
        );
    }

    private static void agregar() throws SQLException {
        Camionero camionero = leerDatos(0);
        dao.insertar(camionero);
        System.out.println("Camionero guardado correctamente.");
    }

    private static void listar() throws SQLException {
        List<Camionero> camioneros = dao.listar();

        if (camioneros.isEmpty()) {
            System.out.println("No hay camioneros registrados.");
            return;
        }

        System.out.println(
            "ID | NOMBRE | TELEFONO | DIRECCION | SALARIO | POBLACION"
        );

        for (Camionero c : camioneros) {
            System.out.println(c);
        }
    }

    private static void modificar() throws SQLException {
        listar();

        System.out.print("ID del camionero que desea modificar: ");
        int id = Integer.parseInt(entrada.nextLine().trim());

        Camionero actual = dao.buscarPorId(id);

        if (actual == null) {
            System.out.println("No existe un camionero con ese ID.");
            return;
        }

        System.out.println("Datos actuales: " + actual);
        System.out.println("Ingrese todos los datos nuevos:");

        Camionero nuevo = leerDatos(id);

        if (dao.actualizar(nuevo)) {
            System.out.println("Camionero actualizado correctamente.");
        } else {
            System.out.println("No se reportaron cambios.");
        }
    }

    private static void eliminar() throws SQLException {
        listar();

        System.out.print("ID del camionero que desea eliminar: ");
        int id = Integer.parseInt(entrada.nextLine().trim());

        Camionero actual = dao.buscarPorId(id);

        if (actual == null) {
            System.out.println("No existe un camionero con ese ID.");
            return;
        }

        System.out.println("Camionero seleccionado: " + actual);
        System.out.print("Para confirmar escriba SI: ");

        if (!entrada.nextLine().trim().equalsIgnoreCase("SI")) {
            System.out.println("Eliminacion cancelada.");
            return;
        }

        if (dao.eliminar(id)) {
            System.out.println("Camionero eliminado correctamente.");
        } else {
            System.out.println("No se elimino ningun registro.");
        }
    }
}