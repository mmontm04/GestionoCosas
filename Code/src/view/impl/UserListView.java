package view.impl;

import model.entities.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class UserListView extends JFrame {
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JButton btnNuevo;
    private JButton btnEliminar;
    private JButton btnCerrar;

    public UserListView() {
        setTitle("Gestión de Usuarios");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Tabla
        String[] columnas = {"ID", "Usuario", "Rol"};
        modeloTabla = new DefaultTableModel(columnas, 0);
        tabla = new JTable(modeloTabla);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // Botones
        JPanel panel = new JPanel();
        btnNuevo = new JButton("Nuevo Usuario");
        btnEliminar = new JButton("Eliminar Seleccionado");
        btnCerrar = new JButton("Cerrar");
        
        panel.add(btnNuevo);
        panel.add(btnEliminar);
        panel.add(btnCerrar);
        add(panel, BorderLayout.SOUTH);

        btnCerrar.addActionListener(e -> dispose());
    }

    public void mostrarUsuarios(List<User> usuarios) {
        modeloTabla.setRowCount(0);
        for (User u : usuarios) {
            modeloTabla.addRow(new Object[]{u.getId(), u.getUsername(), u.getRole()});
        }
    }

    // Getters para el controlador
    public JButton getBtnNuevo() { return btnNuevo; }
    public JButton getBtnEliminar() { return btnEliminar; }
    
    public int getIdSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) return -1;
        return (int) modeloTabla.getValueAt(fila, 0);
    }
}