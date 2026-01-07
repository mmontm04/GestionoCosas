package controller.impl;

import model.dao.OrderDAO;
import model.dao.ProductDAO; // Necesario para cargar lista en el combo
import model.entities.Order;
import model.entities.Product;
import util.SessionController;
import view.impl.OrderFormView;
import view.impl.OrderListView;
import javax.swing.JOptionPane;
import java.util.List;
import java.util.Map;

public class OrderController {
    private OrderDAO dao;
    private OrderListView view;

    public OrderController() {
        this.dao = new OrderDAO();
        this.view = new OrderListView();
        
        this.view.getBtnNuevo().addActionListener(e -> mostrarFormularioCrear());
        this.view.getBtnRecibir().addActionListener(e -> recibirPedidoSeleccionado());
    }

    public void init() {
        // Bloqueo de seguridad (Solo Gerentes suelen hacer pedidos)
        if (!SessionController.getInstance().esGerente()) {
             JOptionPane.showMessageDialog(null, "Acceso denegado: Solo Gerentes.");
             return;
        }
        
        cargarDatos();
        view.setVisible(true);
    }

    private void cargarDatos() {
        List<Order> lista = dao.listarTodos(); 
        view.mostrarPedidos(lista);
    }

    private void recibirPedidoSeleccionado() {
        int idPedido = view.getIdSeleccionado();
        if (idPedido == -1) {
            JOptionPane.showMessageDialog(view, "Selecciona un pedido pendiente.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(view, 
            "¿Confirmar recepción?\nSe sumará el stock de los productos.",
            "Recibir Mercancía", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (dao.marcarRecibido(idPedido)) {
                JOptionPane.showMessageDialog(view, "¡Mercancía recibida! Stock actualizado.");
                cargarDatos(); // Refrescar la tabla para ver el cambio de estado
            } else {
                JOptionPane.showMessageDialog(view, "Error: El pedido ya estaba recibido o hubo un fallo.");
            }
        }
    }

    private void mostrarFormularioCrear() {
        // 1. Necesitamos la lista de productos para el ComboBox
        ProductDAO productoDAO = new ProductDAO();
        List<Product> productos = productoDAO.listarTodos();

        // 2. Abrir formulario
        OrderFormView form = new OrderFormView(view, productos);

        form.addCrearListener(e -> {
            String proveedor = form.getProveedor();
            Map<Product, Double> items = form.getItems();

            if (proveedor.isEmpty() || items.isEmpty()) {
                JOptionPane.showMessageDialog(form, "Falta proveedor o productos.");
                return;
            }

            Order nuevoPedido = new Order();
            nuevoPedido.setProveedor(proveedor);
            // Pasar los items al pedido
            for (Map.Entry<Product, Double> entry : items.entrySet()) {
                nuevoPedido.addItem(entry.getKey(), entry.getValue());
            }

            if (dao.crearPedido(nuevoPedido)) { // Asegúrate que OrderDAO tiene crearPedido(Order)
                JOptionPane.showMessageDialog(form, "Pedido creado correctamente.");
                form.dispose();
                cargarDatos();
            } else {
                JOptionPane.showMessageDialog(form, "Error al guardar el pedido.");
            }
        });

        form.setVisible(true);
    }
}