package umg.vista;

import umg.DAO.CamioneroDAO;
import umg.modelo.Camionero;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.List;

public class VentanaCamionero extends JFrame {

    private final CamioneroDAO dao = new CamioneroDAO();

    private JTextField txtId;
    private JTextField txtNombre;
    private JTextField txtTelefono;
    private JTextField txtDireccion;
    private JTextField txtSalario;
    private JTextField txtPoblacion;

    private JButton btnGuardar;
    private JButton btnBuscar;
    private JButton btnModificar;
    private JButton btnLimpiar;
    private JButton btnEliminar;

    private JTable tblCamioneros;
    private DefaultTableModel modeloTabla;

    public VentanaCamionero() {
        initComponents();
        cargarDatos();
    }

    private void initComponents() {
        setTitle("Mantenimiento de Camioneros");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        // Menú de navegación entre módulos
        JMenuBar mb = new JMenuBar();
        JMenu menu = new JMenu("Módulos");
        JMenuItem miCamiones = new JMenuItem("1. Camiones");
        JMenuItem miCiudades = new JMenuItem("2. Ciudades");
        JMenuItem miCamioneros = new JMenuItem("3. Camioneros (Actual)");
        JMenuItem miPaquetes = new JMenuItem("4. Paquetes");
        JMenuItem miAsignaciones = new JMenuItem("5. Asignaciones de Camiones");

        miCamiones.addActionListener(e -> { new VentanaCamion().setVisible(true); dispose(); });
        miCiudades.addActionListener(e -> { new VentanaCiudad().setVisible(true); dispose(); });
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

        // Fila 1: ID Camionero, Nombre, Teléfono
        JLabel lblId = new JLabel("ID Camionero");
        lblId.setFont(fontLabel);
        lblId.setBounds(40, 20, 100, 20);
        pnl.add(lblId);

        txtId = new JTextField();
        txtId.setFont(fontText);
        txtId.setBounds(40, 42, 95, 26);
        pnl.add(txtId);

        JLabel lblNombre = new JLabel("Nombre");
        lblNombre.setFont(fontLabel);
        lblNombre.setBounds(200, 20, 100, 20);
        pnl.add(lblNombre);

        txtNombre = new JTextField();
        txtNombre.setFont(fontText);
        txtNombre.setBounds(200, 42, 190, 26);
        pnl.add(txtNombre);

        JLabel lblTelefono = new JLabel("Teléfono");
        lblTelefono.setFont(fontLabel);
        lblTelefono.setBounds(430, 20, 100, 20);
        pnl.add(lblTelefono);

        txtTelefono = new JTextField();
        txtTelefono.setFont(fontText);
        txtTelefono.setBounds(430, 42, 130, 26);
        pnl.add(txtTelefono);

        // Fila 2: Dirección, Salario, Población
        JLabel lblDireccion = new JLabel("Dirección");
        lblDireccion.setFont(fontLabel);
        lblDireccion.setBounds(40, 80, 100, 20);
        pnl.add(lblDireccion);

        txtDireccion = new JTextField();
        txtDireccion.setFont(fontText);
        txtDireccion.setBounds(40, 102, 190, 26);
        pnl.add(txtDireccion);

        JLabel lblSalario = new JLabel("Salario");
        lblSalario.setFont(fontLabel);
        lblSalario.setBounds(260, 80, 100, 20);
        pnl.add(lblSalario);

        txtSalario = new JTextField();
        txtSalario.setFont(fontText);
        txtSalario.setBounds(260, 102, 110, 26);
        pnl.add(txtSalario);

        JLabel lblPoblacion = new JLabel("Población");
        lblPoblacion.setFont(fontLabel);
        lblPoblacion.setBounds(400, 80, 100, 20);
        pnl.add(lblPoblacion);

        txtPoblacion = new JTextField();
        txtPoblacion.setFont(fontText);
        txtPoblacion.setBounds(400, 102, 160, 26);
        pnl.add(txtPoblacion);

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
                new Object[]{"ID Camionero", "Nombre", "Teléfono", "Dirección", "Salario", "Población"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblCamioneros = new JTable(modeloTabla);
        tblCamioneros.setRowHeight(22);
        tblCamioneros.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblCamioneros.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblCamioneros.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tblCamioneros.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                seleccionarFila();
            }
        });

        JScrollPane scrollTabla = new JScrollPane(tblCamioneros);
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
            List<Camionero> lista = dao.listar();
            for (Camionero c : lista) {
                modeloTabla.addRow(new Object[]{
                        c.getIdCamionero(),
                        c.getNombre(),
                        c.getTelefono() != null ? c.getTelefono() : "",
                        c.getDireccion() != null ? c.getDireccion() : "",
                        c.getSalario(),
                        c.getPoblacion() != null ? c.getPoblacion() : ""
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar camioneros desde MySQL: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void seleccionarFila() {
        int fila = tblCamioneros.getSelectedRow();
        if (fila >= 0) {
            txtId.setText(modeloTabla.getValueAt(fila, 0).toString());
            txtNombre.setText(modeloTabla.getValueAt(fila, 1).toString());
            Object tel = modeloTabla.getValueAt(fila, 2);
            txtTelefono.setText(tel != null ? tel.toString() : "");
            Object dir = modeloTabla.getValueAt(fila, 3);
            txtDireccion.setText(dir != null ? dir.toString() : "");
            Object sal = modeloTabla.getValueAt(fila, 4);
            txtSalario.setText(sal != null ? sal.toString() : "");
            Object pob = modeloTabla.getValueAt(fila, 5);
            txtPoblacion.setText(pob != null ? pob.toString() : "");
        }
    }

    private Camionero obtenerCamioneroDesdeFormulario(int id) {
        String nombre = txtNombre.getText().trim();
        String tel = txtTelefono.getText().trim();
        String dir = txtDireccion.getText().trim();
        String salStr = txtSalario.getText().trim();
        String pob = txtPoblacion.getText().trim();

        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar el nombre del camionero.", "Aviso", JOptionPane.WARNING_MESSAGE);
            txtNombre.requestFocus();
            return null;
        }

        double salario = 0.0;
        if (!salStr.isEmpty()) {
            try {
                salario = Double.parseDouble(salStr);
                if (salario < 0) {
                    JOptionPane.showMessageDialog(this, "El salario no puede ser negativo.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    txtSalario.requestFocus();
                    return null;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "El salario debe ser un número válido.", "Aviso", JOptionPane.WARNING_MESSAGE);
                txtSalario.requestFocus();
                return null;
            }
        }

        return new Camionero(
                id,
                nombre,
                tel.isEmpty() ? null : tel,
                dir.isEmpty() ? null : dir,
                salario,
                pob.isEmpty() ? null : pob
        );
    }

    private void guardar() {
        Camionero c = obtenerCamioneroDesdeFormulario(0);
        if (c == null) return;

        try {
            dao.insertar(c);
            JOptionPane.showMessageDialog(this, "Camionero guardado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiar();
            cargarDatos();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar camionero: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buscar() {
        String idTexto = txtId.getText().trim();
        if (idTexto.isEmpty()) {
            idTexto = JOptionPane.showInputDialog(this, "Ingrese el ID del camionero que desea buscar:", "Buscar Camionero", JOptionPane.QUESTION_MESSAGE);
            if (idTexto == null || idTexto.trim().isEmpty()) return;
        }

        try {
            int id = Integer.parseInt(idTexto.trim());
            Camionero c = dao.buscarPorId(id);
            if (c != null) {
                txtId.setText(String.valueOf(c.getIdCamionero()));
                txtNombre.setText(c.getNombre());
                txtTelefono.setText(c.getTelefono() != null ? c.getTelefono() : "");
                txtDireccion.setText(c.getDireccion() != null ? c.getDireccion() : "");
                txtSalario.setText(String.valueOf(c.getSalario()));
                txtPoblacion.setText(c.getPoblacion() != null ? c.getPoblacion() : "");

                for (int i = 0; i < tblCamioneros.getRowCount(); i++) {
                    if (Integer.parseInt(tblCamioneros.getValueAt(i, 0).toString()) == id) {
                        tblCamioneros.setRowSelectionInterval(i, i);
                        tblCamioneros.scrollRectToVisible(tblCamioneros.getCellRect(i, 0, true));
                        break;
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "No se encontró ningún camionero con ID " + id, "No encontrado", JOptionPane.INFORMATION_MESSAGE);
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
            JOptionPane.showMessageDialog(this, "Seleccione un camionero de la tabla o búsquelo para modificar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = Integer.parseInt(idTexto);
        Camionero c = obtenerCamioneroDesdeFormulario(id);
        if (c == null) return;

        try {
            if (dao.actualizar(c)) {
                JOptionPane.showMessageDialog(this, "Camionero modificado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
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
            JOptionPane.showMessageDialog(this, "Seleccione un camionero de la tabla para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = Integer.parseInt(idTexto);
        String nombre = txtNombre.getText().trim();

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar al camionero '" + nombre + "' (ID: " + id + ")?",
                "Confirmar Eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (dao.eliminar(id)) {
                    JOptionPane.showMessageDialog(this, "Camionero eliminado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    limpiar();
                    cargarDatos();
                } else {
                    JOptionPane.showMessageDialog(this, "No se encontró el registro para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
                }
            } catch (SQLException ex) {
                if (ex.getErrorCode() == 1451) {
                    JOptionPane.showMessageDialog(this,
                            "No se puede eliminar el camionero porque tiene paquetes o camiones asignados.",
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
        txtTelefono.setText("");
        txtDireccion.setText("");
        txtSalario.setText("");
        txtPoblacion.setText("");
        tblCamioneros.clearSelection();
        txtNombre.requestFocus();
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            new VentanaCamionero().setVisible(true);
        });
    }
}