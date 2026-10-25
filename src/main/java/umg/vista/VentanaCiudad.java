package umg.vista;

import umg.DAO.CiudadDAO;
import umg.modelo.Ciudad;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.List;

public class VentanaCiudad extends JFrame {

    private final CiudadDAO dao = new CiudadDAO();

    private JTextField txtId;
    private JTextField txtNombre;

    private JButton btnGuardar;
    private JButton btnBuscar;
    private JButton btnModificar;
    private JButton btnLimpiar;
    private JButton btnEliminar;

    private JTable tblCiudades;
    private DefaultTableModel modeloTabla;

    public VentanaCiudad() {
        initComponents();
        cargarDatos();
    }

    private void initComponents() {
        setTitle("Mantenimiento de Ciudades");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        // Menú de navegación entre módulos
        JMenuBar mb = new JMenuBar();
        JMenu menu = new JMenu("Módulos");
        JMenuItem miCamiones = new JMenuItem("1. Camiones");
        JMenuItem miCiudades = new JMenuItem("2. Ciudades (Actual)");
        JMenuItem miCamioneros = new JMenuItem("3. Camioneros");
        JMenuItem miPaquetes = new JMenuItem("4. Paquetes");
        JMenuItem miAsignaciones = new JMenuItem("5. Asignaciones de Camiones");

        miCamiones.addActionListener(e -> { new VentanaCamion().setVisible(true); dispose(); });
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

        // Fila 1: ID Ciudad, Nombre
        JLabel lblId = new JLabel("ID Ciudad");
        lblId.setFont(fontLabel);
        lblId.setBounds(40, 20, 100, 20);
        pnl.add(lblId);

        txtId = new JTextField();
        txtId.setFont(fontText);
        txtId.setBounds(40, 42, 95, 26);
        pnl.add(txtId);

        JLabel lblNombre = new JLabel("Nombre");
        lblNombre.setFont(fontLabel);
        lblNombre.setBounds(230, 20, 100, 20);
        pnl.add(lblNombre);

        txtNombre = new JTextField();
        txtNombre.setFont(fontText);
        txtNombre.setBounds(230, 42, 220, 26);
        pnl.add(txtNombre);

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
        modeloTabla = new DefaultTableModel(new Object[]{"ID Ciudad", "Nombre"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblCiudades = new JTable(modeloTabla);
        tblCiudades.setRowHeight(22);
        tblCiudades.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblCiudades.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblCiudades.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tblCiudades.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                seleccionarFila();
            }
        });

        JScrollPane scrollTabla = new JScrollPane(tblCiudades);
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

    private void cargarDatos() {
        modeloTabla.setRowCount(0);
        try {
            List<Ciudad> lista = dao.listar();
            for (Ciudad c : lista) {
                modeloTabla.addRow(new Object[]{c.getIdCiudad(), c.getNombre()});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar ciudades desde MySQL: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void seleccionarFila() {
        int fila = tblCiudades.getSelectedRow();
        if (fila >= 0) {
            txtId.setText(modeloTabla.getValueAt(fila, 0).toString());
            txtNombre.setText(modeloTabla.getValueAt(fila, 1).toString());
        }
    }

    private void guardar() {
        String nombre = txtNombre.getText().trim();
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar el nombre de la ciudad.", "Aviso", JOptionPane.WARNING_MESSAGE);
            txtNombre.requestFocus();
            return;
        }

        try {
            Ciudad c = new Ciudad(0, nombre);
            dao.insertar(c);
            JOptionPane.showMessageDialog(this, "Ciudad guardada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiar();
            cargarDatos();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buscar() {
        String idTexto = txtId.getText().trim();
        if (idTexto.isEmpty()) {
            idTexto = JOptionPane.showInputDialog(this, "Ingrese el ID de la ciudad a buscar:", "Buscar Ciudad", JOptionPane.QUESTION_MESSAGE);
            if (idTexto == null || idTexto.trim().isEmpty()) return;
        }

        try {
            int id = Integer.parseInt(idTexto.trim());
            Ciudad c = dao.buscarPorId(id);
            if (c != null) {
                txtId.setText(String.valueOf(c.getIdCiudad()));
                txtNombre.setText(c.getNombre());

                for (int i = 0; i < tblCiudades.getRowCount(); i++) {
                    if (Integer.parseInt(tblCiudades.getValueAt(i, 0).toString()) == id) {
                        tblCiudades.setRowSelectionInterval(i, i);
                        tblCiudades.scrollRectToVisible(tblCiudades.getCellRect(i, 0, true));
                        break;
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "No se encontró ninguna ciudad con ID " + id, "No encontrado", JOptionPane.INFORMATION_MESSAGE);
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
            JOptionPane.showMessageDialog(this, "Seleccione una ciudad de la tabla o búsquela para modificar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = Integer.parseInt(idTexto);
        String nombre = txtNombre.getText().trim();
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre no puede estar vacío.", "Aviso", JOptionPane.WARNING_MESSAGE);
            txtNombre.requestFocus();
            return;
        }

        try {
            Ciudad c = new Ciudad(id, nombre);
            if (dao.actualizar(c)) {
                JOptionPane.showMessageDialog(this, "Ciudad modificada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
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
            JOptionPane.showMessageDialog(this, "Seleccione una ciudad de la tabla para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = Integer.parseInt(idTexto);
        String nombre = txtNombre.getText().trim();

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar la ciudad '" + nombre + "' (ID: " + id + ")?",
                "Confirmar Eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (dao.eliminar(id)) {
                    JOptionPane.showMessageDialog(this, "Ciudad eliminada correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    limpiar();
                    cargarDatos();
                } else {
                    JOptionPane.showMessageDialog(this, "No se encontró el registro para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
                }
            } catch (SQLException ex) {
                if (ex.getErrorCode() == 1451) {
                    JOptionPane.showMessageDialog(this,
                            "No se puede eliminar la ciudad porque tiene paquetes asociados en la base de datos.",
                            "Restricción de Integridad", JOptionPane.WARNING_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Error al eliminar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void limpiar() {
        txtId.setText("");
        txtNombre.setText("");
        tblCiudades.clearSelection();
        txtNombre.requestFocus();
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            new VentanaCiudad().setVisible(true);
        });
    }
}