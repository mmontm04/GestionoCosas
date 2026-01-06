package view.impl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class ProductFormView extends JDialog { // Usamos JDialog para que sea una ventana secundaria
    private JTextField txtNombre;
    private JTextField txtStock;
    private JTextField txtMinimo;
    private JTextField txtPrecio;
    private JButton btnGuardar;
    private JButton btnCancelar;

    public ProductFormView(Frame parent) {
        super(parent, "Nuevo Producto", true); // true = modal (bloquea la ventana de atrás)
        setSize(400, 300);
        setLocationRelativeTo(parent);
        setLayout(new GridLayout(5, 2, 10, 10));

        add(new JLabel("  Nombre:"));
        txtNombre = new JTextField();
        add(txtNombre);

        add(new JLabel("  Stock Inicial:"));
        txtStock = new JTextField("0");
        add(txtStock);

        add(new JLabel("  Stock Mínimo:"));
        txtMinimo = new JTextField("5");
        add(txtMinimo);

        add(new JLabel("  Precio (€):"));
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
    public String getNombre() { return txtNombre.getText(); }
    public String getStock() { return txtStock.getText(); }
    public String getMinimo() { return txtMinimo.getText(); }
    public String getPrecio() { return txtPrecio.getText(); }

    public void addGuardarListener(ActionListener listener) {
        btnGuardar.addActionListener(listener);
    }

    public void showMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg);
    }
}