package umg.UI;

public class MenuPrincipal {

    public static void main(String[] args) {
        boolean continuar = true;

        while (continuar) {
            System.out.println();
            System.out.println("================================");
            System.out.println("     SISTEMA DE PAQUETERIA");
            System.out.println("================================");
            System.out.println("1. Camiones");
            System.out.println("2. Camioneros");
            System.out.println("3. Paquetes");
            System.out.println("4. Asignaciones de camiones");
            System.out.println("5. Ciudades");
            System.out.println("6. Abrir Interfaz Grafica (Ventana)");
            System.out.println("0. Salir del sistema");
            System.out.print("Seleccione una opcion: ");

            String opcion = Entrada.TECLADO.nextLine().trim();

            switch (opcion) {
                case "1" -> AppTransporteUmg.main(new String[0]);
                case "2" -> PruebaCamionero.main(new String[0]);
                case "3" -> PruebaPaquete.main(new String[0]);
                case "4" -> PruebaAsignacion.main(new String[0]);
                case "5" -> PruebaCiudad.main(new String[0]);
                case "6" -> umg.vista.VentanaPrincipal.main(new String[0]);
                case "0" -> continuar = false;
                default -> System.out.println(
                    "Opcion incorrecta. Intente nuevamente."
                );
            }
        }

        System.out.println("Sistema finalizado.");
    }
}
