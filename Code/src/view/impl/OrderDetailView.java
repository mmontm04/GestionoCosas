package view.impl;

import model.entities.Order;
import model.entities.Product;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Map;

public class OrderDetailView extends JDialog {
    private JTable tabla;
    private DefaultTableModel modelo;
    private JButton btnGuardar;
    private JButton btnCerrar;
    private boolean guardado = false; // Para saber si el usuario dio a guardar

    public OrderDetailView(Frame parent, Order pedido) {
        super(parent, "Detalle del Pedido #" + pedido.getId(), true); // Modal
        setSize(600, 400);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // 1. Configurar Tabla
        // Columnas: ID Prod, Nombre, Cantidad (Editable)
        String[] colNames = {"ID", "Producto", "Cantidad"};
        boolean esEditable = "PENDIENTE".equals(pedido.getEstado());

        modelo = new DefaultTableModel(colNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Solo la columna 2 (Cantidad) es editable y SOLO si está PENDIENTE
                return column == 2 && esEditable;
            }
            
            // Forzar que la columna cantidad sea numérica
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if(columnIndex == 2) return Double.class;
                return String.class;
            }
        };

        // Llenar datos
        for (Map.Entry<Product, Double> entry : pedido.getItems().entrySet()) {
            modelo.addRow(new Object[]{
                entry.getKey().getId(),
                entry.getKey().getNombre(),
                entry.getValue()
            });
        }

        tabla = new JTable(modelo);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // 2. Botones
        JPanel panelSur = new JPanel();
        btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose());
        
        if (esEditable) {
            btnGuardar = new JButton("Guardar Cambios");
            btnGuardar.setBackground(new Color(200, 255, 200));
            btnGuardar.addActionListener(e -> {
                guardado = true;
                actualizarModeloPedido(pedido); // Pasar datos de tabla a objeto
                dispose();
            });
            panelSur.add(btnGuardar);
            
            // Aviso visual
            JLabel aviso = new JLabel(" (Doble clic en cantidad para editar) ");
            aviso.setForeground(Color.BLUE);
            panelSur.add(aviso);
        }

        panelSur.add(btnCerrar);
        add(panelSur, BorderLayout.SOUTH);
    }

    private void actualizarModeloPedido(Order pedido) {
        // Recorremos la tabla y actualizamos el mapa del pedido
        for (int i = 0; i < modelo.getRowCount(); i++) {
            int idProd = (int) modelo.getValueAt(i, 0);
            double cantidad = Double.parseDouble(modelo.getValueAt(i, 2).toString());
            
            // Buscar el producto en el mapa original y actualizar cantidad
            for (Product p : pedido.getItems().keySet()) {
                if (p.getId() == idProd) {
                    pedido.getItems().put(p, cantidad);
                    break;
                }
            }
        }
    }

    public boolean isGuardado() { return guardado; }
}