package view.impl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class ProductFormView extends JDialog { // Usamos JDialog para que sea una ventana secundaria
    private JLabel lblNombre;
    private JLabel lblStock;
    private JLabel lblMinimo;
    private JLabel lblPrecio;
    private JTextField txtNombre;
    private JTextField txtStock;
    private JTextField txtMinimo;
    private JTextField txtPrecio;
    private JButton btnGuardar;
    private JButton btnCancelar;
    private int productId;

    public ProductFormView(Frame parent) {
        super(parent, "Nuevo Producto", true); // true = modal (bloquea la ventana de atrás)
        setSize(400, 300);
        setLocationRelativeTo(parent);
        setLayout(new GridLayout(5, 2, 10, 10));
        productId = 0;

        lblNombre = new JLabel("  Nombre:");
        add(lblNombre);
        txtNombre = new JTextField();
        add(txtNombre);

        lblStock = new JLabel("  Stock Inicial:");
        add(lblStock);
        txtStock = new JTextField("0");
        add(txtStock);

        lblMinimo = new JLabel("  Stock Mínimo:");
        add(lblMinimo);
        txtMinimo = new JTextField("5");
        add(txtMinimo);

        lblPrecio = new JLabel("  Precio (€):");
        add(lblPrecio);
        txtPrecio = new JTextField("0.0");
        add(txtPrecio);

        btnGuardar = new JButton("Guardar");
        add(btnGuardar);
        
        btnCancelar = new JButton("Cancelar");
        add(btnCancelar);
        
        // Cerrar al cancelar
        btnCancelar.addActionListener(e -> dispose());
    }

    // Getters para sacar la información
    public int getProductId() { return productId; }
    public String getNombre() { return txtNombre.getText(); }
    public String getStock() { return txtStock.getText(); }
    public String getMinimo() { return txtMinimo.getText(); }
    public String getPrecio() { return txtPrecio.getText(); }

    public void setProducto(int id, String nombre, double stock, double minimo, double precio) {
        this.productId = id;
        setTitle("Editar Producto");
        lblStock.setText("  Stock Actual:");
        btnGuardar.setText("Guardar cambios");
        txtNombre.setText(nombre);
        txtStock.setText(String.valueOf(stock));
        txtMinimo.setText(String.valueOf(minimo));
        txtPrecio.setText(String.valueOf(precio));
    }

    public void addGuardarListener(ActionListener listener) {
        btnGuardar.addActionListener(listener);
    }

    public void showMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg);
    }
}
