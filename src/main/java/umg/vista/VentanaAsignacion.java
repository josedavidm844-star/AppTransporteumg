package umg.vista;

import umg.DAO.CamionDAO;
import umg.DAO.CamioneroCamionDAO;
import umg.DAO.CamioneroDAO;
import umg.modelo.Camion;
import umg.modelo.Camionero;
import umg.modelo.CamioneroCamion;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VentanaAsignacion extends JFrame {

    private final CamioneroCamionDAO asignacionDAO = new CamioneroCamionDAO();
    private final CamioneroDAO camioneroDAO = new CamioneroDAO();
    private final CamionDAO camionDAO = new CamionDAO();

    private JTextField txtId;
    private JComboBox<ComboItem> cbxCamionero;
    private JComboBox<ComboItem> cbxCamion;

    private JButton btnGuardar;
    private JButton btnBuscar;
    private JButton btnModificar;
    private JButton btnLimpiar;
    private JButton btnEliminar;

    private JTable tblAsignaciones;
    private DefaultTableModel modeloTabla;

    private final Map<Integer, String> mapaCamioneros = new HashMap<>();
    private final Map<Integer, String> mapaCamiones = new HashMap<>();

    public VentanaAsignacion() {
        initComponents();
        cargarCombos();
        cargarDatos();
    }

    private void initComponents() {
        setTitle("Mantenimiento de Asignación de Camiones");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        // Menú de navegación entre módulos
        JMenuBar mb = new JMenuBar();
        JMenu menu = new JMenu("Módulos");
        JMenuItem miCamiones = new JMenuItem("1. Camiones");
        JMenuItem miCiudades = new JMenuItem("2. Ciudades");
        JMenuItem miCamioneros = new JMenuItem("3. Camioneros");
        JMenuItem miPaquetes = new JMenuItem("4. Paquetes");
        JMenuItem miAsignaciones = new JMenuItem("5. Asignaciones de Camiones (Actual)");

        miCamiones.addActionListener(e -> { new VentanaCamion().setVisible(true); dispose(); });
        miCiudades.addActionListener(e -> { new VentanaCiudad().setVisible(true); dispose(); });
        miCamioneros.addActionListener(e -> { new VentanaCamionero().setVisible(true); dispose(); });
        miPaquetes.addActionListener(e -> { new VentanaPaquete().setVisible(true); dispose(); });

        menu.add(miCamiones);
        menu.add(miCiudades);
        menu.add(miCamioneros);
        menu.add(miPaquetes);
        menu.add(miAsignaciones);
        mb.add(menu);
        setJMenuBar(mb);

        JPanel pnl = new JPanel(null);
        pnl.setPreferredSize(new Dimension(720, 480));

        Font fontLabel = new Font("Segoe UI", Font.PLAIN, 13);
        Font fontText = new Font("Segoe UI", Font.PLAIN, 13);
        Font fontBtn = new Font("Segoe UI", Font.PLAIN, 13);

        // Fila 1: ID Asignación, Camionero, Camión Asignado
        JLabel lblId = new JLabel("ID Asignación");
        lblId.setFont(fontLabel);
        lblId.setBounds(40, 20, 100, 20);
        pnl.add(lblId);

        txtId = new JTextField();
        txtId.setFont(fontText);
        txtId.setBounds(40, 42, 95, 26);
        pnl.add(txtId);

        JLabel lblCam = new JLabel("Camionero");
        lblCam.setFont(fontLabel);
        lblCam.setBounds(180, 20, 100, 20);
        pnl.add(lblCam);

        cbxCamionero = new JComboBox<>();
        cbxCamionero.setFont(fontText);
        cbxCamionero.setBounds(180, 42, 220, 26);
        pnl.add(cbxCamionero);

        JLabel lblCamion = new JLabel("Camión Asignado");
        lblCamion.setFont(fontLabel);
        lblCamion.setBounds(430, 20, 130, 20);
        pnl.add(lblCamion);

        cbxCamion = new JComboBox<>();
        cbxCamion.setFont(fontText);
        cbxCamion.setBounds(430, 42, 220, 26);
        pnl.add(cbxCamion);

        // Fila 2: Botones (Guardar, Buscar, Modificar, Limpiar, Eliminar)
        btnGuardar = new JButton("Guardar");
        btnGuardar.setFont(fontBtn);
        btnGuardar.setBounds(50, 110, 90, 30);
        pnl.add(btnGuardar);

        btnBuscar = new JButton("Buscar");
        btnBuscar.setFont(fontBtn);
        btnBuscar.setBounds(160, 110, 90, 30);
        pnl.add(btnBuscar);

        btnModificar = new JButton("Modificar");
        btnModificar.setFont(fontBtn);
        btnModificar.setBounds(270, 110, 95, 30);
        pnl.add(btnModificar);

        btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setFont(fontBtn);
        btnLimpiar.setBounds(385, 110, 90, 30);
        pnl.add(btnLimpiar);

        btnEliminar = new JButton("Eliminar");
        btnEliminar.setFont(fontBtn);
        btnEliminar.setBounds(495, 110, 90, 30);
        pnl.add(btnEliminar);

        // Fila 3: Tabla dentro de JScrollPane
        modeloTabla = new DefaultTableModel(
                new Object[]{"ID Asignación", "Camionero", "Camión"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblAsignaciones = new JTable(modeloTabla);
        tblAsignaciones.setRowHeight(22);
        tblAsignaciones.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblAsignaciones.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblAsignaciones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tblAsignaciones.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                seleccionarFila();
            }
        });

        JScrollPane scrollTabla = new JScrollPane(tblAsignaciones);
        scrollTabla.setBounds(50, 160, 620, 290);
        pnl.add(scrollTabla);

        setContentPane(pnl);
        pack();
        setLocationRelativeTo(null);

        // Eventos
        btnGuardar.addActionListener(e -> guardar());
        btnBuscar.addActionListener(e -> buscar());
        btnModificar.addActionListener(e -> modificar());
        btnLimpiar.addActionListener(e -> limpiar());
        btnEliminar.addActionListener(e -> eliminar());
    }

    private void cargarCombos() {
        cbxCamionero.removeAllItems();
        mapaCamioneros.clear();
        try {
            List<Camionero> camioneros = camioneroDAO.listar();
            for (Camionero c : camioneros) {
                mapaCamioneros.put(c.getIdCamionero(), c.getNombre());
                cbxCamionero.addItem(new ComboItem(c.getIdCamionero(), c.getIdCamionero() + " - " + c.getNombre()));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar camioneros: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        cbxCamion.removeAllItems();
        mapaCamiones.clear();
        cbxCamion.addItem(new ComboItem(0, "-- Sin camión --"));
        try {
            List<Camion> camiones = camionDAO.listar();
            for (Camion c : camiones) {
                String desc = c.getNoPlaca() + " (" + c.getModelo() + ")";
                mapaCamiones.put(c.getIdCamion(), desc);
                cbxCamion.addItem(new ComboItem(c.getIdCamion(), c.getIdCamion() + " - " + desc));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar camiones: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarDatos() {
        modeloTabla.setRowCount(0);
        try {
            List<CamioneroCamion> lista = asignacionDAO.listar();
            for (CamioneroCamion asig : lista) {
                String nomCamionero = mapaCamioneros.getOrDefault(asig.getIdCamionero(), "ID " + asig.getIdCamionero());
                String descCamion = asig.getIdCamion() != null
                        ? mapaCamiones.getOrDefault(asig.getIdCamion(), "ID " + asig.getIdCamion())
                        : "Sin camión";

                modeloTabla.addRow(new Object[]{
                        asig.getIdCamioneroCamion(),
                        nomCamionero,
                        descCamion
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar asignaciones desde MySQL: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void seleccionarFila() {
        int fila = tblAsignaciones.getSelectedRow();
        if (fila >= 0) {
            txtId.setText(modeloTabla.getValueAt(fila, 0).toString());

            String nomCamionero = modeloTabla.getValueAt(fila, 1).toString();
            seleccionarEnComboPorTexto(cbxCamionero, nomCamionero);

            String descCamion = modeloTabla.getValueAt(fila, 2).toString();
            seleccionarEnComboPorTexto(cbxCamion, descCamion);
        }
    }

    private void seleccionarEnComboPorId(JComboBox<ComboItem> combo, int id) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (combo.getItemAt(i).getId() == id) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    private void seleccionarEnComboPorTexto(JComboBox<ComboItem> combo, String texto) {
        if ("Sin camión".equalsIgnoreCase(texto) || "-- Sin camión --".equalsIgnoreCase(texto)) {
            combo.setSelectedIndex(0);
            return;
        }
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (combo.getItemAt(i).getLabel().contains(texto)) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    private CamioneroCamion obtenerAsignacionDesdeFormulario(int id) {
        ComboItem itemCamionero = (ComboItem) cbxCamionero.getSelectedItem();
        if (itemCamionero == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un camionero.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        ComboItem itemCamion = (ComboItem) cbxCamion.getSelectedItem();
        Integer idCamion = (itemCamion != null && itemCamion.getId() > 0) ? itemCamion.getId() : null;

        return new CamioneroCamion(id, itemCamionero.getId(), idCamion);
    }

    private void guardar() {
        CamioneroCamion asig = obtenerAsignacionDesdeFormulario(0);
        if (asig == null) return;

        try {
            asignacionDAO.insertar(asig);
            JOptionPane.showMessageDialog(this, "Asignación guardada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiar();
            cargarDatos();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar asignación: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buscar() {
        String idTexto = txtId.getText().trim();
        if (idTexto.isEmpty()) {
            idTexto = JOptionPane.showInputDialog(this, "Ingrese el ID de la asignación que desea buscar:", "Buscar Asignación", JOptionPane.QUESTION_MESSAGE);
            if (idTexto == null || idTexto.trim().isEmpty()) return;
        }

        try {
            int id = Integer.parseInt(idTexto.trim());
            CamioneroCamion asig = asignacionDAO.buscarPorId(id);
            if (asig != null) {
                txtId.setText(String.valueOf(asig.getIdCamioneroCamion()));
                seleccionarEnComboPorId(cbxCamionero, asig.getIdCamionero());
                if (asig.getIdCamion() != null) {
                    seleccionarEnComboPorId(cbxCamion, asig.getIdCamion());
                } else {
                    cbxCamion.setSelectedIndex(0);
                }

                for (int i = 0; i < tblAsignaciones.getRowCount(); i++) {
                    if (Integer.parseInt(tblAsignaciones.getValueAt(i, 0).toString()) == id) {
                        tblAsignaciones.setRowSelectionInterval(i, i);
                        tblAsignaciones.scrollRectToVisible(tblAsignaciones.getCellRect(i, 0, true));
                        break;
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "No se encontró ninguna asignación con ID " + id, "No encontrado", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El ID debe ser un número entero.", "Aviso", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al buscar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void modificar() {
        String idTexto = txtId.getText().trim();
        if (idTexto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione una asignación de la tabla o búsquela para modificar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = Integer.parseInt(idTexto);
        CamioneroCamion asig = obtenerAsignacionDesdeFormulario(id);
        if (asig == null) return;

        try {
            if (asignacionDAO.actualizar(asig)) {
                JOptionPane.showMessageDialog(this, "Asignación modificada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                limpiar();
                cargarDatos();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo modificar el registro.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al modificar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminar() {
        String idTexto = txtId.getText().trim();
        if (idTexto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione una asignación de la tabla para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = Integer.parseInt(idTexto);

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar la asignación con ID: " + id + "?",
                "Confirmar Eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (asignacionDAO.eliminar(id)) {
                    JOptionPane.showMessageDialog(this, "Asignación eliminada correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    limpiar();
                    cargarDatos();
                } else {
                    JOptionPane.showMessageDialog(this, "No se encontró el registro para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al eliminar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void limpiar() {
        txtId.setText("");
        if (cbxCamionero.getItemCount() > 0) cbxCamionero.setSelectedIndex(0);
        if (cbxCamion.getItemCount() > 0) cbxCamion.setSelectedIndex(0);
        tblAsignaciones.clearSelection();
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            new VentanaAsignacion().setVisible(true);
        });
    }
}