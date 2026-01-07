package view.impl;

import model.entities.Product;
import util.patterns.strategy.IStockViewStrategy;
import util.patterns.strategy.LowStockHighlightStrategy;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ProductListView extends JFrame {
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JButton btnNuevo;
    private JButton btnEditar;
    private JButton btnEliminar;
    private JButton btnCerrar;
    private IStockViewStrategy stockViewStrategy;

    public ProductListView() {
        setTitle("Catálogo de Productos");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 1. Configurar columnas de la tabla
        String[] columnas = {"ID", "Nombre", "Stock", "Mínimo", "Precio (€)"};
        modeloTabla = new DefaultTableModel(columnas, 0);
        tabla = new JTable(modeloTabla);
        stockViewStrategy = new LowStockHighlightStrategy();
        stockViewStrategy.apply(tabla);
        
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // 2. Panel de botones inferior
        JPanel panelBotones = new JPanel();
        
        btnNuevo = new JButton("Nuevo Producto");
        panelBotones.add(btnNuevo);

        btnEditar = new JButton("Editar Producto");
        panelBotones.add(btnEditar);

        btnEliminar = new JButton("Eliminar Producto");
        panelBotones.add(btnEliminar);
        
        btnCerrar = new JButton("Cerrar");
        panelBotones.add(btnCerrar);
        
        add(panelBotones, BorderLayout.SOUTH);

        // Acción básica del botón cerrar
        btnCerrar.addActionListener(e -> dispose());
    }

    public JButton getBtnNuevo() {
        return btnNuevo;
    }

    public JButton getBtnEditar() {
        return btnEditar;
    }

    public JButton getBtnEliminar() {
        return btnEliminar;
    }

    public Integer getProductoSeleccionadoId() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            return null;
        }
        Object value = tabla.getValueAt(fila, 0);
        return (value instanceof Number) ? ((Number) value).intValue() : Integer.parseInt(value.toString());
    }

    public void showMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg);
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
