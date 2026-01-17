package controller.impl;

import model.dao.OrderDAO;
import model.dao.ProductDAO;
import model.entities.Order;
import model.entities.Product;
import util.SessionController;
import view.impl.OrderFormView;
import view.impl.OrderListView;
import view.impl.OrderDetailView; 
import javax.swing.JOptionPane;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;

public class OrderController {
    private OrderDAO dao;
    private OrderListView view;

    public OrderController() {
        this.dao = new OrderDAO();
        this.view = new OrderListView();
        
        // 1. Listeners de Botones
        this.view.getBtnNuevo().addActionListener(e -> mostrarFormularioCrear());
        this.view.getBtnParcial().addActionListener(e -> gestionarParcial());
        this.view.getBtnRecibir().addActionListener(e -> recibirPedidoSeleccionado());

        this.view.getTabla().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // Si hace doble clic
                    abrirDetallePedido();
                }
            }
        });
    }

    public void init() {
        // Bloqueo de seguridad (Solo Gerentes)
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
                cargarDatos(); // Refrescar la tabla
            } else {
                JOptionPane.showMessageDialog(view, "Error: El pedido ya estaba recibido o hubo un fallo.");
            }
        }
    }

    private void mostrarFormularioCrear() {
        ProductDAO productoDAO = new ProductDAO();
        List<Product> productos = productoDAO.listarTodos();

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
            
            for (Map.Entry<Product, Double> entry : items.entrySet()) {
                nuevoPedido.addItem(entry.getKey(), entry.getValue());
            }

            if (dao.crearPedido(nuevoPedido)) { 
                JOptionPane.showMessageDialog(form, "Pedido creado correctamente.");
                form.dispose();
                cargarDatos();
            } else {
                JOptionPane.showMessageDialog(form, "Error al guardar el pedido.");
            }
        });

        form.setVisible(true);
    }

    private void gestionarParcial() {
        int id = view.getIdSeleccionado();
        if (id == -1) {
            JOptionPane.showMessageDialog(view, "Selecciona un pedido pendiente.");
            return;
        }

        Order ordenOriginal = dao.obtenerPedidoCompleto(id);
        
        if (ordenOriginal == null) return;
        if (!"PENDIENTE".equals(ordenOriginal.getEstado())) {
            JOptionPane.showMessageDialog(view, "Solo se pueden dividir pedidos PENDIENTES.");
            return;
        }

        Map<Product, Double> cantidadesRecibidas = new java.util.HashMap<>();
        boolean alMenosUno = false;

        for (Map.Entry<Product, Double> linea : ordenOriginal.getItems().entrySet()) {
            Product p = linea.getKey();
            Double cantidadOriginal = linea.getValue();

            String input = JOptionPane.showInputDialog(view, 
                "Producto: " + p.getNombre() + "\n" +
                "Esperado: " + cantidadOriginal + "\n" +
                "¿Cuánto ha llegado realmente?", 
                cantidadOriginal); 

            if (input == null) return; 

            try {
                double cantLlegada = Double.parseDouble(input);
                
                if (cantLlegada < 0 || cantLlegada > cantidadOriginal) {
                    JOptionPane.showMessageDialog(view, "Cantidad inválida.");
                    return;
                }
                
                cantidadesRecibidas.put(p, cantLlegada);
                if (cantLlegada > 0) alMenosUno = true;

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(view, "Introduce un número válido.");
                return;
            }
        }

        if (!alMenosUno) {
            JOptionPane.showMessageDialog(view, "Operación cancelada.");
            return;
        }

        if (dao.procesarEntregaParcial(ordenOriginal, cantidadesRecibidas)) {
            JOptionPane.showMessageDialog(view, 
                "✅ Entrega Parcial registrada.\n" +
                "- Stock sumado.\n" +
                "- Pedido restante creado.");
            cargarDatos();
        } else {
            JOptionPane.showMessageDialog(view, "Error al procesar la entrega parcial.");
        }
    }

    private void abrirDetallePedido() {
        int id = view.getIdSeleccionado();
        if (id == -1) return;

        Order pedido = dao.obtenerPedidoCompleto(id);
        if (pedido == null) return;

        OrderDetailView detailView = new OrderDetailView(view, pedido);
        detailView.setVisible(true);

        if (detailView.isGuardado()) {
            if (dao.actualizarCantidades(pedido)) {
                JOptionPane.showMessageDialog(view, "Cantidades actualizadas correctamente.");
                cargarDatos(); // Refrescar la tabla principal
            } else {
                JOptionPane.showMessageDialog(view, "Error al actualizar en la base de datos.");
            }
        }
    }
}