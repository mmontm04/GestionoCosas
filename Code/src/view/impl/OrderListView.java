package view.impl;

import model.entities.Order;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class OrderListView extends JFrame {
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JButton btnNuevo;
    private JButton btnRecibir;
    private JButton btnCerrar;

    public OrderListView() {
        setTitle("Gestión de Pedidos a Proveedores");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 1. Tabla
        String[] columnas = {"ID", "Fecha", "Proveedor", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override // Hacer celdas no editables
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabla = new JTable(modeloTabla);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // 2. Botones
        JPanel panelBotones = new JPanel();
        btnNuevo = new JButton("Nuevo Pedido");
        btnRecibir = new JButton("Recibir Mercancía (Stock)");
        btnRecibir.setBackground(new Color(200, 255, 200)); // Verde clarito
        btnCerrar = new JButton("Cerrar");

        panelBotones.add(btnNuevo);
        panelBotones.add(btnRecibir);
        panelBotones.add(btnCerrar);
        add(panelBotones, BorderLayout.SOUTH);

        // Cerrar básico
        btnCerrar.addActionListener(e -> dispose());
    }

    public void mostrarPedidos(List<Order> pedidos) {
        modeloTabla.setRowCount(0);
        for (Order o : pedidos) {
            modeloTabla.addRow(new Object[]{
                o.getId(),
                o.getFecha(), 
                o.getProveedor(),
                o.getEstado()
            });
        }
    }

    public int getIdSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) return -1;
        return (int) modeloTabla.getValueAt(fila, 0);
    }

    // Getters para el controlador
    public JButton getBtnNuevo() { return btnNuevo; }
    public JButton getBtnRecibir() { return btnRecibir; }
}