package umg.UI;

import umg.DAO.CamioneroDAO;
import umg.DAO.CiudadDAO;
import umg.DAO.PaqueteDAO;
import umg.modelo.Camionero;
import umg.modelo.Ciudad;
import umg.modelo.Paquete;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class PruebaPaquete {

    private static final Scanner entrada = Entrada.TECLADO;
    private static final PaqueteDAO dao = new PaqueteDAO();

    public static void main(String[] args) {
        boolean continuar = true;

        while (continuar) {
            System.out.println();
            System.out.println("===== PAQUETES =====");
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
                    "Ingrese numeros enteros validos para los ID."
                );

            } catch (IllegalArgumentException e) {
                System.out.println(
                    "Dato incorrecto: " + e.getMessage()
                );

            } catch (SQLException e) {
                if (e.getErrorCode() == 1452) {
                    System.out.println(
                        "La ciudad o el camionero ya no existen."
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

    private static boolean mostrarReferencias() throws SQLException {
        List<Camionero> camioneros = new CamioneroDAO().listar();
        List<Ciudad> ciudades = new CiudadDAO().listar();

        if (camioneros.isEmpty() || ciudades.isEmpty()) {
            System.out.println(
                "Primero debe registrar al menos un camionero y una ciudad."
            );
            return false;
        }

        System.out.println();
        System.out.println("CAMIONEROS DISPONIBLES:");

        for (Camionero c : camioneros) {
            System.out.println(
                c.getIdCamionero() + " - " + c.getNombre()
            );
        }

        System.out.println("CIUDADES DISPONIBLES:");

        for (Ciudad c : ciudades) {
            System.out.println(
                c.getIdCiudad() + " - " + c.getNombre()
            );
        }

        return true;
    }

    private static Paquete leerDatos(int id) {
        System.out.print("Descripcion: ");
        String descripcion = entrada.nextLine();

        System.out.print("Destinatario: ");
        String destinatario = entrada.nextLine();

        System.out.print("Direccion de envio: ");
        String direccion = entrada.nextLine();

        System.out.print("ID del camionero: ");
        int idCamionero =
            Integer.parseInt(entrada.nextLine().trim());

        System.out.print("ID de la ciudad: ");
        int idCiudad =
            Integer.parseInt(entrada.nextLine().trim());

        return new Paquete(
            id, descripcion, destinatario,
            direccion, idCamionero, idCiudad
        );
    }

    private static void agregar() throws SQLException {
        if (!mostrarReferencias()) {
            return;
        }

        dao.insertar(leerDatos(0));
        System.out.println("Paquete guardado correctamente.");
    }

    private static void listar() throws SQLException {
        List<Paquete> paquetes = dao.listar();

        if (paquetes.isEmpty()) {
            System.out.println("No hay paquetes registrados.");
            return;
        }

        System.out.println(
            "ID | DESCRIPCION | DESTINATARIO | DIRECCION"
            + " | ID CAMIONERO | ID CIUDAD"
        );

        for (Paquete p : paquetes) {
            System.out.println(p);
        }
    }

    private static void modificar() throws SQLException {
        listar();

        System.out.print("ID del paquete que desea modificar: ");
        int id = Integer.parseInt(entrada.nextLine().trim());

        Paquete actual = dao.buscarPorId(id);

        if (actual == null) {
            System.out.println("No existe un paquete con ese ID.");
            return;
        }

        System.out.println("Datos actuales: " + actual);

        if (!mostrarReferencias()) {
            return;
        }

        System.out.println("Ingrese todos los datos nuevos:");

        if (dao.actualizar(leerDatos(id))) {
            System.out.println("Paquete actualizado correctamente.");
        } else {
            System.out.println("No se reportaron cambios.");
        }
    }

    private static void eliminar() throws SQLException {
        listar();

        System.out.print("ID del paquete que desea eliminar: ");
        int id = Integer.parseInt(entrada.nextLine().trim());

        Paquete actual = dao.buscarPorId(id);

        if (actual == null) {
            System.out.println("No existe un paquete con ese ID.");
            return;
        }

        System.out.println("Paquete seleccionado: " + actual);
        System.out.print("Para confirmar escriba SI: ");

        if (!entrada.nextLine().trim().equalsIgnoreCase("SI")) {
            System.out.println("Eliminacion cancelada.");
            return;
        }

        if (dao.eliminar(id)) {
            System.out.println("Paquete eliminado correctamente.");
        } else {
            System.out.println("No se elimino ningun registro.");
        }
    }
}