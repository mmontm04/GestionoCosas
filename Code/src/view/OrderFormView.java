package view;

import model.entities.Product;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderFormView extends JDialog {
    private JTextField txtProveedor;
    private JComboBox<Product> comboProductos;
    private JTextField txtCantidad;
    private JButton btnAddLinea;
    private JTable tablaItems;
    private DefaultTableModel modeloItems;
    private JButton btnCrearPedido;
    
    // Almacen temporal de items del pedido (Producto -> Cantidad)
    private Map<Product, Double> itemsTemporales = new HashMap<>();

    public OrderFormView(Frame parent, List<Product> productosDisponibles) {
        super(parent, "Nuevo Pedido a Proveedor", true);
        setSize(600, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        // --- PANEL SUPERIOR: Proveedor ---
        JPanel panelCabecera = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelCabecera.add(new JLabel("Proveedor:"));
        txtProveedor = new JTextField(20);
        panelCabecera.add(txtProveedor);
        add(panelCabecera, BorderLayout.NORTH);

        // --- PANEL CENTRAL: Selección de Productos ---
        JPanel panelCentral = new JPanel(new BorderLayout());
        
        // Selector
        JPanel panelSelector = new JPanel(new FlowLayout());
        comboProductos = new JComboBox<>(productosDisponibles.toArray(new Product[0]));
        txtCantidad = new JTextField("1", 5);
        btnAddLinea = new JButton("Añadir al Pedido");
        
        panelSelector.add(new JLabel("Producto:"));
        panelSelector.add(comboProductos);
        panelSelector.add(new JLabel("Cant:"));
        panelSelector.add(txtCantidad);
        panelSelector.add(btnAddLinea);
        
        panelCentral.add(panelSelector, BorderLayout.NORTH);

        // Tabla de items
        modeloItems = new DefaultTableModel(new String[]{"Producto", "Cantidad"}, 0);
        tablaItems = new JTable(modeloItems);
        panelCentral.add(new JScrollPane(tablaItems), BorderLayout.CENTER);

        add(panelCentral, BorderLayout.CENTER);

        // --- PANEL INFERIOR: Confirmar ---
        btnCrearPedido = new JButton("ENVIAR PEDIDO");
        add(btnCrearPedido, BorderLayout.SOUTH);

        // --- Lógica interna visual ---
        btnAddLinea.addActionListener(e -> agregarLineaVisual());
    }

    private void agregarLineaVisual() {
        Product p = (Product) comboProductos.getSelectedItem();
        try {
            double cant = Double.parseDouble(txtCantidad.getText());
            if (cant <= 0) throw new NumberFormatException();

            itemsTemporales.put(p, cant);
            modeloItems.addRow(new Object[]{p.getNombre(), cant});
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Cantidad inválida");
        }
    }

    public String getProveedor() { return txtProveedor.getText(); }
    public Map<Product, Double> getItems() { return itemsTemporales; }

    public void addCrearListener(ActionListener l) {
        btnCrearPedido.addActionListener(l);
    }
}