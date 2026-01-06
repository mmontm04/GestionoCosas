package view.impl;

import model.entities.Product;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ProductListView extends JFrame {
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JButton btnNuevo;
    private JButton btnCerrar;

    public ProductListView() {
        setTitle("Catálogo de Productos");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 1. Configurar columnas de la tabla
        String[] columnas = {"ID", "Nombre", "Stock", "Mínimo", "Precio (€)"};
        modeloTabla = new DefaultTableModel(columnas, 0);
        tabla = new JTable(modeloTabla);
        
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // 2. Panel de botones inferior
        JPanel panelBotones = new JPanel();
        
        btnNuevo = new JButton("Nuevo Producto");
        panelBotones.add(btnNuevo);
        
        btnCerrar = new JButton("Cerrar");
        panelBotones.add(btnCerrar);
        
        add(panelBotones, BorderLayout.SOUTH);

        // Acción básica del botón cerrar
        btnCerrar.addActionListener(e -> dispose());
    }

    public JButton getBtnNuevo() {
        return btnNuevo;
    }

    // Método para llenar la tabla con datos reales
    public void mostrarProductos(List<Product> productos) {
        modeloTabla.setRowCount(0); // Limpiar tabla
        for (Product p : productos) {
            Object[] fila = {
                p.getId(),
                p.getNombre(),
                p.getStockActual(),
                p.getStockMinimo(),
                p.getPrecio()
            };
            modeloTabla.addRow(fila);
        }
    }
}