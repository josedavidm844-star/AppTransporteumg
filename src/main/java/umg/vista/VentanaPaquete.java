package umg.vista;

import umg.DAO.CamioneroDAO;
import umg.DAO.CiudadDAO;
import umg.DAO.PaqueteDAO;
import umg.modelo.Camionero;
import umg.modelo.Ciudad;
import umg.modelo.Paquete;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VentanaPaquete extends JFrame {

    private final PaqueteDAO paqueteDAO = new PaqueteDAO();
    private final CamioneroDAO camioneroDAO = new CamioneroDAO();
    private final CiudadDAO ciudadDAO = new CiudadDAO();

    private JTextField txtId;
    private JTextField txtDescripcion;
    private JTextField txtDestinatario;
    private JTextField txtDireccionEnvio;
    private JComboBox<ComboItem> cbxCamionero;
    private JComboBox<ComboItem> cbxCiudad;

    private JButton btnGuardar;
    private JButton btnBuscar;
    private JButton btnModificar;
    private JButton btnLimpiar;
    private JButton btnEliminar;

    private JTable tblPaquetes;
    private DefaultTableModel modeloTabla;

    private final Map<Integer, String> mapaCamioneros = new HashMap<>();
    private final Map<Integer, String> mapaCiudades = new HashMap<>();

    public VentanaPaquete() {
        initComponents();
        cargarCombos();
        cargarDatos();
    }

    private void initComponents() {
        setTitle("Mantenimiento de Paquetes");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        // Menú de navegación entre módulos
        JMenuBar mb = new JMenuBar();
        JMenu menu = new JMenu("Módulos");
        JMenuItem miCamiones = new JMenuItem("1. Camiones");
        JMenuItem miCiudades = new JMenuItem("2. Ciudades");
        JMenuItem miCamioneros = new JMenuItem("3. Camioneros");
        JMenuItem miPaquetes = new JMenuItem("4. Paquetes (Actual)");
        JMenuItem miAsignaciones = new JMenuItem("5. Asignaciones de Camiones");

        miCamiones.addActionListener(e -> { new VentanaCamion().setVisible(true); dispose(); });
        miCiudades.addActionListener(e -> { new VentanaCiudad().setVisible(true); dispose(); });
        miCamioneros.addActionListener(e -> { new VentanaCamionero().setVisible(true); dispose(); });
        miAsignaciones.addActionListener(e -> { new VentanaAsignacion().setVisible(true); dispose(); });

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

        // Fila 1: ID Paquete, Descripción, Destinatario
        JLabel lblId = new JLabel("ID Paquete");
        lblId.setFont(fontLabel);
        lblId.setBounds(40, 20, 100, 20);
        pnl.add(lblId);

        txtId = new JTextField();
        txtId.setFont(fontText);
        txtId.setBounds(40, 42, 95, 26);
        pnl.add(txtId);

        JLabel lblDesc = new JLabel("Descripción");
        lblDesc.setFont(fontLabel);
        lblDesc.setBounds(180, 20, 100, 20);
        pnl.add(lblDesc);

        txtDescripcion = new JTextField();
        txtDescripcion.setFont(fontText);
        txtDescripcion.setBounds(180, 42, 180, 26);
        pnl.add(txtDescripcion);

        JLabel lblDest = new JLabel("Destinatario");
        lblDest.setFont(fontLabel);
        lblDest.setBounds(400, 20, 100, 20);
        pnl.add(lblDest);

        txtDestinatario = new JTextField();
        txtDestinatario.setFont(fontText);
        txtDestinatario.setBounds(400, 42, 180, 26);
        pnl.add(txtDestinatario);

        // Fila 2: Dirección Envío, Camionero Asignado, Ciudad Destino
        JLabel lblDir = new JLabel("Dirección Envío");
        lblDir.setFont(fontLabel);
        lblDir.setBounds(40, 80, 110, 20);
        pnl.add(lblDir);

        txtDireccionEnvio = new JTextField();
        txtDireccionEnvio.setFont(fontText);
        txtDireccionEnvio.setBounds(40, 102, 180, 26);
        pnl.add(txtDireccionEnvio);

        JLabel lblCam = new JLabel("Camionero");
        lblCam.setFont(fontLabel);
        lblCam.setBounds(250, 80, 120, 20);
        pnl.add(lblCam);

        cbxCamionero = new JComboBox<>();
        cbxCamionero.setFont(fontText);
        cbxCamionero.setBounds(250, 102, 170, 26);
        pnl.add(cbxCamionero);

        JLabel lblCiu = new JLabel("Ciudad Destino");
        lblCiu.setFont(fontLabel);
        lblCiu.setBounds(440, 80, 120, 20);
        pnl.add(lblCiu);

        cbxCiudad = new JComboBox<>();
        cbxCiudad.setFont(fontText);
        cbxCiudad.setBounds(440, 102, 160, 26);
        pnl.add(cbxCiudad);

        // Fila 3: Botones (Guardar, Buscar, Modificar, Limpiar, Eliminar)
        btnGuardar = new JButton("Guardar");
        btnGuardar.setFont(fontBtn);
        btnGuardar.setBounds(50, 150, 90, 30);
        pnl.add(btnGuardar);

        btnBuscar = new JButton("Buscar");
        btnBuscar.setFont(fontBtn);
        btnBuscar.setBounds(160, 150, 90, 30);
        pnl.add(btnBuscar);

        btnModificar = new JButton("Modificar");
        btnModificar.setFont(fontBtn);
        btnModificar.setBounds(270, 150, 95, 30);
        pnl.add(btnModificar);

        btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setFont(fontBtn);
        btnLimpiar.setBounds(385, 150, 90, 30);
        pnl.add(btnLimpiar);

        btnEliminar = new JButton("Eliminar");
        btnEliminar.setFont(fontBtn);
        btnEliminar.setBounds(495, 150, 90, 30);
        pnl.add(btnEliminar);

        // Fila 4: Tabla dentro de JScrollPane
        modeloTabla = new DefaultTableModel(
                new Object[]{"ID Paquete", "Descripción", "Destinatario", "Dirección Envío", "Camionero", "Ciudad"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblPaquetes = new JTable(modeloTabla);
        tblPaquetes.setRowHeight(22);
        tblPaquetes.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblPaquetes.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblPaquetes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tblPaquetes.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                seleccionarFila();
            }
        });

        JScrollPane scrollTabla = new JScrollPane(tblPaquetes);
        scrollTabla.setBounds(50, 200, 620, 250);
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

        cbxCiudad.removeAllItems();
        mapaCiudades.clear();
        try {
            List<Ciudad> ciudades = ciudadDAO.listar();
            for (Ciudad c : ciudades) {
                mapaCiudades.put(c.getIdCiudad(), c.getNombre());
                cbxCiudad.addItem(new ComboItem(c.getIdCiudad(), c.getIdCiudad() + " - " + c.getNombre()));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar ciudades: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarDatos() {
        modeloTabla.setRowCount(0);
        try {
            List<Paquete> lista = paqueteDAO.listar();
            for (Paquete p : lista) {
                String nomCamionero = mapaCamioneros.getOrDefault(p.getIdCamionero(), "ID " + p.getIdCamionero());
                String nomCiudad = mapaCiudades.getOrDefault(p.getIdCiudad(), "ID " + p.getIdCiudad());

                modeloTabla.addRow(new Object[]{
                        p.getIdPaquete(),
                        p.getDescripcion(),
                        p.getDestinatario(),
                        p.getDireccionEnvio(),
                        nomCamionero,
                        nomCiudad
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar paquetes desde MySQL: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void seleccionarFila() {
        int fila = tblPaquetes.getSelectedRow();
        if (fila >= 0) {
            txtId.setText(modeloTabla.getValueAt(fila, 0).toString());
            txtDescripcion.setText(modeloTabla.getValueAt(fila, 1).toString());
            txtDestinatario.setText(modeloTabla.getValueAt(fila, 2).toString());
            txtDireccionEnvio.setText(modeloTabla.getValueAt(fila, 3).toString());

            String nomCamionero = modeloTabla.getValueAt(fila, 4).toString();
            seleccionarEnComboPorTexto(cbxCamionero, nomCamionero);

            String nomCiudad = modeloTabla.getValueAt(fila, 5).toString();
            seleccionarEnComboPorTexto(cbxCiudad, nomCiudad);
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
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (combo.getItemAt(i).getLabel().contains(texto)) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    private Paquete obtenerPaqueteDesdeFormulario(int id) {
        String descripcion = txtDescripcion.getText().trim();
        String destinatario = txtDestinatario.getText().trim();
        String direccion = txtDireccionEnvio.getText().trim();

        if (descripcion.isEmpty()) {
            JOptionPane.showMessageDialog(this, "La descripción del paquete es obligatoria.", "Aviso", JOptionPane.WARNING_MESSAGE);
            txtDescripcion.requestFocus();
            return null;
        }

        if (destinatario.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El destinatario es obligatorio.", "Aviso", JOptionPane.WARNING_MESSAGE);
            txtDestinatario.requestFocus();
            return null;
        }

        if (direccion.isEmpty()) {
            JOptionPane.showMessageDialog(this, "La dirección de envío es obligatoria.", "Aviso", JOptionPane.WARNING_MESSAGE);
            txtDireccionEnvio.requestFocus();
            return null;
        }

        ComboItem itemCamionero = (ComboItem) cbxCamionero.getSelectedItem();
        if (itemCamionero == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un camionero.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        ComboItem itemCiudad = (ComboItem) cbxCiudad.getSelectedItem();
        if (itemCiudad == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una ciudad.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        return new Paquete(id, descripcion, destinatario, direccion, itemCamionero.getId(), itemCiudad.getId());
    }

    private void guardar() {
        Paquete p = obtenerPaqueteDesdeFormulario(0);
        if (p == null) return;

        try {
            paqueteDAO.insertar(p);
            JOptionPane.showMessageDialog(this, "Paquete guardado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiar();
            cargarDatos();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar paquete: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buscar() {
        String idTexto = txtId.getText().trim();
        if (idTexto.isEmpty()) {
            idTexto = JOptionPane.showInputDialog(this, "Ingrese el ID del paquete que desea buscar:", "Buscar Paquete", JOptionPane.QUESTION_MESSAGE);
            if (idTexto == null || idTexto.trim().isEmpty()) return;
        }

        try {
            int id = Integer.parseInt(idTexto.trim());
            Paquete p = paqueteDAO.buscarPorId(id);
            if (p != null) {
                txtId.setText(String.valueOf(p.getIdPaquete()));
                txtDescripcion.setText(p.getDescripcion());
                txtDestinatario.setText(p.getDestinatario());
                txtDireccionEnvio.setText(p.getDireccionEnvio());
                seleccionarEnComboPorId(cbxCamionero, p.getIdCamionero());
                seleccionarEnComboPorId(cbxCiudad, p.getIdCiudad());

                for (int i = 0; i < tblPaquetes.getRowCount(); i++) {
                    if (Integer.parseInt(tblPaquetes.getValueAt(i, 0).toString()) == id) {
                        tblPaquetes.setRowSelectionInterval(i, i);
                        tblPaquetes.scrollRectToVisible(tblPaquetes.getCellRect(i, 0, true));
                        break;
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "No se encontró ningún paquete con ID " + id, "No encontrado", JOptionPane.INFORMATION_MESSAGE);
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
            JOptionPane.showMessageDialog(this, "Seleccione un paquete de la tabla o búsquelo para modificar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = Integer.parseInt(idTexto);
        Paquete p = obtenerPaqueteDesdeFormulario(id);
        if (p == null) return;

        try {
            if (paqueteDAO.actualizar(p)) {
                JOptionPane.showMessageDialog(this, "Paquete modificado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
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
            JOptionPane.showMessageDialog(this, "Seleccione un paquete de la tabla para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = Integer.parseInt(idTexto);
        String desc = txtDescripcion.getText().trim();

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar el paquete '" + desc + "' (ID: " + id + ")?",
                "Confirmar Eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (paqueteDAO.eliminar(id)) {
                    JOptionPane.showMessageDialog(this, "Paquete eliminado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
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
        txtDescripcion.setText("");
        txtDestinatario.setText("");
        txtDireccionEnvio.setText("");
        if (cbxCamionero.getItemCount() > 0) cbxCamionero.setSelectedIndex(0);
        if (cbxCiudad.getItemCount() > 0) cbxCiudad.setSelectedIndex(0);
        tblPaquetes.clearSelection();
        txtDescripcion.requestFocus();
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            new VentanaPaquete().setVisible(true);
        });
    }
}