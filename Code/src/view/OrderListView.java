package view;

import model.entities.Order;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class OrderListView extends JFrame {
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JButton btnNuevo;
    private JButton btnRecibir;
    private JButton btnParcial;
    private JButton btnCerrar;

    public OrderListView() {
        setTitle("Gestión de Pedidos a Proveedores");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 1. Tabla
        String[] columnas = {"ID", "Fecha", "Proveedor", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override 
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabla = new JTable(modeloTabla);

        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                // 1. Obtenemos la celda base
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                Object estadoObj = table.getValueAt(row, 3);
                String estado = (estadoObj != null) ? estadoObj.toString() : "";

                if (!isSelected) {
                    if ("PARCIAL".equals(estado)) {
                        c.setBackground(new Color(255, 255, 200)); // Amarillo suave
                    } else if ("RECIBIDO".equals(estado)) {
                        c.setBackground(new Color(200, 255, 200)); // Verde suave
                    } else {
                        c.setBackground(Color.WHITE); // Blanco normal
                    }
                    c.setForeground(Color.BLACK);
                }

                return c;
            }
        });

        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // 2. Botones
        JPanel panelBotones = new JPanel();
        
        btnNuevo = new JButton("Nuevo Pedido");
        
        btnParcial = new JButton("Entrega Parcial");
        btnParcial.setBackground(new Color(255, 255, 200)); // Mismo amarillo
        
        btnRecibir = new JButton("Recibir Mercancía (Stock)");
        btnRecibir.setBackground(new Color(200, 255, 200)); // Mismo verde
        
        btnCerrar = new JButton("Cerrar");

        panelBotones.add(btnNuevo);
        panelBotones.add(btnParcial);
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

    // Getters
    public JButton getBtnNuevo() { return btnNuevo; }
    public JButton getBtnRecibir() { return btnRecibir; }
    public JButton getBtnParcial() { return btnParcial; }
    public JTable getTabla() { return tabla; }
}