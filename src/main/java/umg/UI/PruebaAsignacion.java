package umg.UI;

import umg.DAO.CamionDAO;
import umg.DAO.CamioneroDAO;
import umg.DAO.CamioneroCamionDAO;
import umg.modelo.Camion;
import umg.modelo.Camionero;
import umg.modelo.CamioneroCamion;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class PruebaAsignacion {

    private static final Scanner entrada = Entrada.TECLADO;

    private static final CamioneroCamionDAO dao =
        new CamioneroCamionDAO();

    public static void main(String[] args) {
        boolean continuar = true;

        while (continuar) {
            System.out.println();
            System.out.println("===== ASIGNACIONES =====");
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
                        "El camionero o el camion ya no existen."
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

    private static boolean mostrarReferencias()
            throws SQLException {

        List<Camionero> camioneros = new CamioneroDAO().listar();
        List<Camion> camiones = new CamionDAO().listar();

        if (camioneros.isEmpty()) {
            System.out.println(
                "Primero debe registrar un camionero."
            );
            return false;
        }

        System.out.println("CAMIONEROS DISPONIBLES:");

        for (Camionero c : camioneros) {
            System.out.println(
                c.getIdCamionero() + " - " + c.getNombre()
            );
        }

        System.out.println("CAMIONES DISPONIBLES:");

        if (camiones.isEmpty()) {
            System.out.println(
                "No hay camiones. Puede dejar el ID del camion vacio."
            );
        }

        for (Camion c : camiones) {
            System.out.println(
                c.getIdCamion() + " - " + c.getNoPlaca()
            );
        }

        return true;
    }

    private static CamioneroCamion leerDatos(int id) {
        System.out.print("ID del camionero: ");
        int idCamionero =
            Integer.parseInt(entrada.nextLine().trim());

        System.out.print(
            "ID del camion (Enter para dejar sin camion): "
        );
        String texto = entrada.nextLine().trim();

        Integer idCamion = null;

        if (!texto.isEmpty()) {
            idCamion = Integer.valueOf(texto);
        }

        return new CamioneroCamion(
            id, idCamionero, idCamion
        );
    }

    private static void agregar() throws SQLException {
        if (!mostrarReferencias()) {
            return;
        }

        dao.insertar(leerDatos(0));
        System.out.println("Asignacion guardada correctamente.");
    }

    private static void listar() throws SQLException {
        List<CamioneroCamion> asignaciones = dao.listar();

        if (asignaciones.isEmpty()) {
            System.out.println("No hay asignaciones registradas.");
            return;
        }

        System.out.println(
            "ID ASIGNACION | ID CAMIONERO | ID CAMION"
        );

        for (CamioneroCamion a : asignaciones) {
            System.out.println(a);
        }
    }

    private static void modificar() throws SQLException {
        listar();

        System.out.print("ID de la asignacion que desea modificar: ");
        int id = Integer.parseInt(entrada.nextLine().trim());

        CamioneroCamion actual = dao.buscarPorId(id);

        if (actual == null) {
            System.out.println("No existe una asignacion con ese ID.");
            return;
        }

        System.out.println("Datos actuales: " + actual);

        if (!mostrarReferencias()) {
            return;
        }

        System.out.println("Ingrese los datos nuevos:");

        if (dao.actualizar(leerDatos(id))) {
            System.out.println("Asignacion actualizada correctamente.");
        } else {
            System.out.println("No se reportaron cambios.");
        }
    }

    private static void eliminar() throws SQLException {
        listar();

        System.out.print("ID de la asignacion que desea eliminar: ");
        int id = Integer.parseInt(entrada.nextLine().trim());

        CamioneroCamion actual = dao.buscarPorId(id);

        if (actual == null) {
            System.out.println("No existe una asignacion con ese ID.");
            return;
        }

        System.out.println("Asignacion seleccionada: " + actual);
        System.out.print("Para confirmar escriba SI: ");

        if (!entrada.nextLine().trim().equalsIgnoreCase("SI")) {
            System.out.println("Eliminacion cancelada.");
            return;
        }

        if (dao.eliminar(id)) {
            System.out.println("Asignacion eliminada correctamente.");
        } else {
            System.out.println("No se elimino ningun registro.");
        }
    }
}