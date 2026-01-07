package view.impl;

import model.entities.Product;
import model.entities.Recipe;
import model.entities.RecipeIngredient;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class ManagerRecipesFormView extends JDialog {
    // Campos Receta
    private JTextField txtNombre;
    private JTextArea txtDescripcion;
    private JTextField txtPrecio;
    
    // Campos Ingredientes
    private JComboBox<Product> comboProductos;
    private JTextField txtCantidad;
    private JButton btnAddIngrediente;
    private JButton btnRemoveIngrediente;
    private JTable tablaIngredientes;
    private DefaultTableModel modeloTablaIng;
    
    // Botones principales
    private JButton btnGuardar;
    
    // Lista temporal para guardar lo que vamos añadiendo
    private List<RecipeIngredient> ingredientesTemporales = new ArrayList<>();

    public ManagerRecipesFormView(Frame parent, List<Product> productosDisponibles) {
        super(parent, "Nueva Receta Completa", true);
        setSize(600, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        // --- PANEL SUPERIOR: Datos Receta ---
        JPanel panelDatos = new JPanel(new GridLayout(3, 2, 5, 5));
        panelDatos.setBorder(BorderFactory.createTitledBorder("Datos Generales"));
        
        panelDatos.add(new JLabel("Nombre Plato:"));
        txtNombre = new JTextField();
        panelDatos.add(txtNombre);
        
        panelDatos.add(new JLabel("Precio Venta:"));
        txtPrecio = new JTextField("0.0");
        panelDatos.add(txtPrecio);
        
        panelDatos.add(new JLabel("Descripción:"));
        txtDescripcion = new JTextArea(2, 20);
        panelDatos.add(new JScrollPane(txtDescripcion));
        
        add(panelDatos, BorderLayout.NORTH);

        // --- PANEL CENTRAL: Ingredientes ---
        JPanel panelIngredientes = new JPanel(new BorderLayout());
        panelIngredientes.setBorder(BorderFactory.createTitledBorder("Ingredientes"));

        // Selector
        JPanel panelSelector = new JPanel(new FlowLayout());
        comboProductos = new JComboBox<>(productosDisponibles.toArray(new Product[0]));
        txtCantidad = new JTextField("1", 5);
        btnAddIngrediente = new JButton("Añadir (+)");
        btnRemoveIngrediente = new JButton("Quitar (-)");
        
        panelSelector.add(new JLabel("Producto:"));
        panelSelector.add(comboProductos);
        panelSelector.add(new JLabel("Cant:"));
        panelSelector.add(txtCantidad);
        panelSelector.add(btnAddIngrediente);
        panelSelector.add(btnRemoveIngrediente);
        
        panelIngredientes.add(panelSelector, BorderLayout.NORTH);

        // Tabla visual
        modeloTablaIng = new DefaultTableModel(new String[]{"Producto", "Cantidad"}, 0);
        tablaIngredientes = new JTable(modeloTablaIng);
        panelIngredientes.add(new JScrollPane(tablaIngredientes), BorderLayout.CENTER);

        add(panelIngredientes, BorderLayout.CENTER);

        // --- PANEL INFERIOR: Guardar ---
        btnGuardar = new JButton("GUARDAR RECETA COMPLETA");
        btnGuardar.setFont(new Font("Arial", Font.BOLD, 14));
        add(btnGuardar, BorderLayout.SOUTH);

        // --- LÓGICA INTERNA VISUAL ---
        btnAddIngrediente.addActionListener(e -> agregarIngredienteVisual());
        btnRemoveIngrediente.addActionListener(e -> quitarIngredienteVisual());
    }

    private void agregarIngredienteVisual() {
        Product p = (Product) comboProductos.getSelectedItem();
        try {
            double cant = Double.parseDouble(txtCantidad.getText());
            
            // Añadir a lista temporal
            ingredientesTemporales.add(new RecipeIngredient(p, cant));
            
            // Añadir a tabla visual
            modeloTablaIng.addRow(new Object[]{p.getNombre(), cant});
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Cantidad inválida");
        }
    }

    private void quitarIngredienteVisual() {
        int fila = tablaIngredientes.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un ingrediente para quitar");
            return;
        }

        ingredientesTemporales.remove(fila);
        modeloTablaIng.removeRow(fila);
    }

    public void setReceta(Recipe receta) {
        setTitle("Editar Receta Completa");
        txtNombre.setText(receta.getNombre());
        txtDescripcion.setText(receta.getDescripcion());
        txtPrecio.setText(String.valueOf(receta.getPrecioVenta()));

        ingredientesTemporales.clear();
        modeloTablaIng.setRowCount(0);
        for (RecipeIngredient ingrediente : receta.getIngredientes()) {
            ingredientesTemporales.add(ingrediente);
            modeloTablaIng.addRow(new Object[]{
                ingrediente.getProducto().getNombre(),
                ingrediente.getCantidad()
            });
        }
    }

    // Getters
    public String getNombre() { return txtNombre.getText(); }
    public String getDescripcion() { return txtDescripcion.getText(); }
    public String getPrecio() { return txtPrecio.getText(); }
    public List<RecipeIngredient> getIngredientesSeleccionados() { return ingredientesTemporales; }

    public void addGuardarListener(ActionListener l) {
        btnGuardar.addActionListener(l);
    }

    public void setGuardarButtonText(String text) {
        btnGuardar.setText(text);
    }
}
