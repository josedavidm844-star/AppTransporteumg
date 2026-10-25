package umg.vista;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class VentanaPrincipal {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            new VentanaCamion().setVisible(true);
        });
    }
}