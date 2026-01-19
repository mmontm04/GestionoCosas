package view;

import model.entities.Recipe;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class RecipeListView extends JFrame {
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JButton btnNuevo;
    private JButton btnEditar;
    private JButton btnEliminar;
    private JButton btnCerrar;

    public RecipeListView() {
        setTitle("Gestión de Recetas (Menú)");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Columnas
        String[] columnas = {"ID", "Nombre del Plato", "Descripción", "P. Venta (€)"};
        modeloTabla = new DefaultTableModel(columnas, 0);
        tabla = new JTable(modeloTabla);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // Botones
        JPanel panel = new JPanel();
        btnNuevo = new JButton("Nueva Receta");
        btnEditar = new JButton("Editar Receta");
        btnEliminar = new JButton("Eliminar Receta");
        btnCerrar = new JButton("Cerrar");
        
        panel.add(btnNuevo);
        panel.add(btnEditar);
        panel.add(btnEliminar);
        panel.add(btnCerrar);
        add(panel, BorderLayout.SOUTH);

        btnCerrar.addActionListener(e -> dispose());
    }

    public void configurarSeguridad(boolean esGerente) {
        if (!esGerente) {
            btnNuevo.setVisible(false); 
            btnEditar.setVisible(false);
            btnEliminar.setVisible(false);
        }
    }
    
    public void mostrarRecetas(List<Recipe> lista) {
        modeloTabla.setRowCount(0);
        for (Recipe r : lista) {
            modeloTabla.addRow(new Object[]{
                r.getId(), r.getNombre(), r.getDescripcion(), r.getPrecioVenta()
            });
        }
    }

    public int getIdRecetaSeleccionada() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) return -1;
        // Asumiendo que la columna 0 es el ID (como definimos antes)
        return (int) modeloTabla.getValueAt(fila, 0); 
    }

    public JButton getBtnNuevo() { return btnNuevo; }
    public JButton getBtnEditar() { return btnEditar; }
    public JButton getBtnEliminar() { return btnEliminar; }
}
