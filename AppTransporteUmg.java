package umg.UI;

import umg.DAO.CamionDAO;
import umg.modelo.Camion;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class AppTransporteUmg {

    private static final Scanner entrada = Entrada.TECLADO;
    private static final CamionDAO camionDAO = new CamionDAO();

    public static void main(String[] args) {
        boolean continuar = true;

        while (continuar) {
            System.out.println();
            System.out.println("===== CAMIONES =====");
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
                    "Ingrese un numero entero valido para ID o potencia."
                );

            } catch (IllegalArgumentException e) {
                System.out.println("Dato incorrecto: " + e.getMessage());

            } catch (SQLException e) {
                if (e.getErrorCode() == 1451) {
                    System.out.println(
                        "No se puede eliminar: el camion tiene "
                        + "registros relacionados."
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

    private static Camion leerDatos(int id) {
        System.out.print("Placa: ");
        String placa = entrada.nextLine();

        System.out.print("Modelo: ");
        String modelo = entrada.nextLine();

        System.out.print("Tipo (Enter para dejar vacio): ");
        String tipo = entrada.nextLine();

        System.out.print("Potencia (Enter para dejar vacio): ");
        String textoPotencia = entrada.nextLine().trim();

        Integer potencia = null;

        if (!textoPotencia.isEmpty()) {
            potencia = Integer.valueOf(textoPotencia);
        }

        return new Camion(id, placa, modelo, tipo, potencia);
    }

    private static void agregar() throws SQLException {
        Camion camion = leerDatos(0);
        camionDAO.insertar(camion);
        System.out.println("Camion guardado correctamente.");
    }

    private static void listar() throws SQLException {
        List<Camion> camiones = camionDAO.listar();

        if (camiones.isEmpty()) {
            System.out.println("No hay camiones registrados.");
            return;
        }

        System.out.println();
        System.out.println("ID | PLACA | MODELO | TIPO | POTENCIA");

        for (Camion camion : camiones) {
            System.out.println(camion);
        }
    }

    private static void modificar() throws SQLException {
        listar();

        System.out.print("ID del camion que desea modificar: ");
        int id = Integer.parseInt(entrada.nextLine().trim());

        if (!existe(id)) {
            System.out.println("No existe un camion con ese ID.");
            return;
        }

        System.out.println("Ingrese todos los datos nuevos:");
        Camion camion = leerDatos(id);

        if (camionDAO.actualizar(camion)) {
            System.out.println("Camion actualizado correctamente.");
        } else {
            System.out.println("No se reportaron cambios.");
        }
    }

    private static void eliminar() throws SQLException {
        listar();

        System.out.print("ID del camion que desea eliminar: ");
        int id = Integer.parseInt(entrada.nextLine().trim());

        if (!existe(id)) {
            System.out.println("No existe un camion con ese ID.");
            return;
        }

        System.out.print(
            "Para confirmar la eliminacion escriba SI: "
        );

        if (!entrada.nextLine().trim().equalsIgnoreCase("SI")) {
            System.out.println("Eliminacion cancelada.");
            return;
        }

        if (camionDAO.eliminar(id)) {
            System.out.println("Camion eliminado correctamente.");
        } else {
            System.out.println("No se elimino ningun registro.");
        }
    }

    private static boolean existe(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException(
                "El ID debe ser mayor que cero."
            );
        }

        for (Camion camion : camionDAO.listar()) {
            if (camion.getIdCamion() == id) {
                return true;
            }
        }

        return false;
    }
}