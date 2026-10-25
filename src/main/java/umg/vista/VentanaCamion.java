package umg.vista;

import umg.DAO.CamionDAO;
import umg.modelo.Camion;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.List;

public class VentanaCamion extends JFrame {

    private final CamionDAO dao = new CamionDAO();

    private JTextField txtId;
    private JTextField txtPlaca;
    private JTextField txtTipo;
    private JTextField txtModelo;
    private JTextField txtPotencia;

    private JButton btnGuardar;
    private JButton btnBuscar;
    private JButton btnModificar;
    private JButton btnLimpiar;
    private JButton btnEliminar;

    private JTable tblCamiones;
    private DefaultTableModel modeloTabla;

    public VentanaCamion() {
        initComponents();
        cargarDatos();
    }

    private void initComponents() {
        setTitle("Mantenimiento de Camiones");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        // Menú de navegación entre módulos
        JMenuBar mb = new JMenuBar();
        JMenu menu = new JMenu("Módulos");
        JMenuItem miCamiones = new JMenuItem("1. Camiones (Actual)");
        JMenuItem miCiudades = new JMenuItem("2. Ciudades");
        JMenuItem miCamioneros = new JMenuItem("3. Camioneros");
        JMenuItem miPaquetes = new JMenuItem("4. Paquetes");
        JMenuItem miAsignaciones = new JMenuItem("5. Asignaciones de Camiones");

        miCiudades.addActionListener(e -> { new VentanaCiudad().setVisible(true); dispose(); });
        miCamioneros.addActionListener(e -> { new VentanaCamionero().setVisible(true); dispose(); });
        miPaquetes.addActionListener(e -> { new VentanaPaquete().setVisible(true); dispose(); });
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

        // Fila 1: ID Camion, No.Placa, Tipo
        JLabel lblId = new JLabel("ID Camion");
        lblId.setFont(fontLabel);
        lblId.setBounds(40, 20, 100, 20);
        pnl.add(lblId);

        txtId = new JTextField();
        txtId.setFont(fontText);
        txtId.setBounds(40, 42, 95, 26);
        pnl.add(txtId);

        JLabel lblPlaca = new JLabel("No.Placa");
        lblPlaca.setFont(fontLabel);
        lblPlaca.setBounds(230, 20, 100, 20);
        pnl.add(lblPlaca);

        txtPlaca = new JTextField();
        txtPlaca.setFont(fontText);
        txtPlaca.setBounds(230, 42, 95, 26);
        pnl.add(txtPlaca);

        JLabel lblTipo = new JLabel("Tipo");
        lblTipo.setFont(fontLabel);
        lblTipo.setBounds(430, 20, 100, 20);
        pnl.add(lblTipo);

        txtTipo = new JTextField();
        txtTipo.setFont(fontText);
        txtTipo.setBounds(430, 42, 95, 26);
        pnl.add(txtTipo);

        // Fila 2: Modelo, Potencia
        JLabel lblModelo = new JLabel("Modelo");
        lblModelo.setFont(fontLabel);
        lblModelo.setBounds(40, 80, 100, 20);
        pnl.add(lblModelo);

        txtModelo = new JTextField();
        txtModelo.setFont(fontText);
        txtModelo.setBounds(40, 102, 95, 26);
        pnl.add(txtModelo);

        JLabel lblPotencia = new JLabel("Potencia");
        lblPotencia.setFont(fontLabel);
        lblPotencia.setBounds(180, 80, 100, 20);
        pnl.add(lblPotencia);

        txtPotencia = new JTextField();
        txtPotencia.setFont(fontText);
        txtPotencia.setBounds(180, 102, 95, 26);
        pnl.add(txtPotencia);

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
                new Object[]{"ID Camion", "No.Placa", "Modelo", "Tipo", "Potencia"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblCamiones = new JTable(modeloTabla);
        tblCamiones.setRowHeight(22);
        tblCamiones.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblCamiones.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblCamiones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tblCamiones.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                seleccionarFila();
            }
        });

        JScrollPane scrollTabla = new JScrollPane(tblCamiones);
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

    private void cargarDatos() {
        modeloTabla.setRowCount(0);
        try {
            List<Camion> lista = dao.listar();
            for (Camion c : lista) {
                modeloTabla.addRow(new Object[]{
                        c.getIdCamion(),
                        c.getNoPlaca(),
                        c.getModelo(),
                        c.getTipo() != null ? c.getTipo() : "",
                        c.getPotencia() != null ? c.getPotencia() : ""
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar camiones desde MySQL: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void seleccionarFila() {
        int fila = tblCamiones.getSelectedRow();
        if (fila >= 0) {
            txtId.setText(modeloTabla.getValueAt(fila, 0).toString());
            txtPlaca.setText(modeloTabla.getValueAt(fila, 1).toString());
            txtModelo.setText(modeloTabla.getValueAt(fila, 2).toString());
            Object tipo = modeloTabla.getValueAt(fila, 3);
            txtTipo.setText(tipo != null ? tipo.toString() : "");
            Object pot = modeloTabla.getValueAt(fila, 4);
            txtPotencia.setText(pot != null ? pot.toString() : "");
        }
    }

    private Camion obtenerCamionDesdeFormulario(int id) {
        String placa = txtPlaca.getText().trim();
        String modelo = txtModelo.getText().trim();
        String tipo = txtTipo.getText().trim();
        String potStr = txtPotencia.getText().trim();

        if (placa.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar el número de placa.", "Aviso", JOptionPane.WARNING_MESSAGE);
            txtPlaca.requestFocus();
            return null;
        }

        if (modelo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar el modelo del camión.", "Aviso", JOptionPane.WARNING_MESSAGE);
            txtModelo.requestFocus();
            return null;
        }

        Integer potencia = null;
        if (!potStr.isEmpty()) {
            try {
                potencia = Integer.parseInt(potStr);
                if (potencia <= 0) {
                    JOptionPane.showMessageDialog(this, "La potencia debe ser mayor a 0.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    txtPotencia.requestFocus();
                    return null;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "La potencia debe ser un número entero.", "Aviso", JOptionPane.WARNING_MESSAGE);
                txtPotencia.requestFocus();
                return null;
            }
        }

        return new Camion(id, placa.toUpperCase(), modelo, tipo.isEmpty() ? null : tipo, potencia);
    }

    private void guardar() {
        Camion c = obtenerCamionDesdeFormulario(0);
        if (c == null) return;

        try {
            dao.insertar(c);
            JOptionPane.showMessageDialog(this, "Camión guardado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiar();
            cargarDatos();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar camión: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buscar() {
        String idTexto = txtId.getText().trim();
        if (idTexto.isEmpty()) {
            idTexto = JOptionPane.showInputDialog(this, "Ingrese el ID del camión que desea buscar:", "Buscar Camión", JOptionPane.QUESTION_MESSAGE);
            if (idTexto == null || idTexto.trim().isEmpty()) return;
        }

        try {
            int id = Integer.parseInt(idTexto.trim());
            Camion c = dao.buscarPorId(id);
            if (c != null) {
                txtId.setText(String.valueOf(c.getIdCamion()));
                txtPlaca.setText(c.getNoPlaca());
                txtModelo.setText(c.getModelo());
                txtTipo.setText(c.getTipo() != null ? c.getTipo() : "");
                txtPotencia.setText(c.getPotencia() != null ? String.valueOf(c.getPotencia()) : "");

                for (int i = 0; i < tblCamiones.getRowCount(); i++) {
                    if (Integer.parseInt(tblCamiones.getValueAt(i, 0).toString()) == id) {
                        tblCamiones.setRowSelectionInterval(i, i);
                        tblCamiones.scrollRectToVisible(tblCamiones.getCellRect(i, 0, true));
                        break;
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "No se encontró ningún camión con ID " + id, "No encontrado", JOptionPane.INFORMATION_MESSAGE);
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
            JOptionPane.showMessageDialog(this, "Seleccione un camión de la tabla o búsquelo para modificar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = Integer.parseInt(idTexto);
        Camion c = obtenerCamionDesdeFormulario(id);
        if (c == null) return;

        try {
            if (dao.actualizar(c)) {
                JOptionPane.showMessageDialog(this, "Camión modificado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
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
            JOptionPane.showMessageDialog(this, "Seleccione un camión de la tabla para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = Integer.parseInt(idTexto);
        String placa = txtPlaca.getText().trim();

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar el camión con placa '" + placa + "' (ID: " + id + ")?",
                "Confirmar Eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (dao.eliminar(id)) {
                    JOptionPane.showMessageDialog(this, "Camión eliminado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    limpiar();
                    cargarDatos();
                } else {
                    JOptionPane.showMessageDialog(this, "No se encontró el registro para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
                }
            } catch (SQLException ex) {
                if (ex.getErrorCode() == 1451) {
                    JOptionPane.showMessageDialog(this,
                            "No se puede eliminar el camión porque tiene asignaciones registradas en la base de datos.",
                            "Restricción de Integridad", JOptionPane.WARNING_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Error al eliminar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void limpiar() {
        txtId.setText("");
        txtPlaca.setText("");
        txtModelo.setText("");
        txtTipo.setText("");
        txtPotencia.setText("");
        tblCamiones.clearSelection();
        txtPlaca.requestFocus();
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            new VentanaCamion().setVisible(true);
        });
    }
}