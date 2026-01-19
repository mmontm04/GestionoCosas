package controller.impl;

import model.dao.ProductDAO;
import model.entities.Product;
import view.ProductFormView;
import view.ProductListView;

import java.util.List;
import javax.swing.JOptionPane;

public class ProductController {
    private ProductListView listView;
    private ProductDAO dao;
    private int paginaActual = 1;
    private final int ITEMS_POR_PAGINA = 5;

    public ProductController() {
        this.listView = new ProductListView();
        this.dao = new ProductDAO();
        
        // Escuchar el botón "Nuevo" de la lista
        this.listView.getBtnNuevo().addActionListener(e -> abrirFormularioCrear());
        this.listView.getBtnEditar().addActionListener(e -> abrirFormularioEditar());
        this.listView.getBtnEliminar().addActionListener(e -> eliminarProductoSeleccionado());
    }

    public void init() {
        cargarDatos();
        listView.setVisible(true);
        this.listView.getBtnAnterior().addActionListener(e -> cambiarPagina(-1));
        this.listView.getBtnSiguiente().addActionListener(e -> cambiarPagina(1));
    }

    private void cargarDatos() {
        int offset = (paginaActual - 1) * ITEMS_POR_PAGINA;

        List<Product> productos = dao.listarPaginado(ITEMS_POR_PAGINA, offset);
        listView.mostrarProductos(productos);

        int totalProductos = dao.contarTotal();
        int totalPaginas = (int) Math.ceil((double) totalProductos / ITEMS_POR_PAGINA);

        if (totalPaginas == 0) totalPaginas = 1; 

        listView.getLblPagina().setText("Página " + paginaActual + " de " + totalPaginas);

        listView.getBtnAnterior().setEnabled(paginaActual > 1);

        listView.getBtnSiguiente().setEnabled(paginaActual < totalPaginas);
    }

    private void cambiarPagina(int delta) {
        paginaActual += delta;
        cargarDatos();
    }

    private void eliminarProductoSeleccionado() {
        Integer productoId = listView.getProductoSeleccionadoId();
        if (productoId == null) {
            listView.showMessage("Selecciona un producto para eliminar.");
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(
            listView,
            "¿Seguro que deseas eliminar el producto seleccionado?",
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION
        );

        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        if (dao.eliminar(productoId)) {
            listView.showMessage("Producto eliminado.");
            cargarDatos();
        } else {
            listView.showMessage("No se pudo eliminar el producto.");
        }
    }

    private void abrirFormularioCrear() {
        ProductFormView form = new ProductFormView(listView);
        
        // Lógica del botón Guardar del formulario
        form.addGuardarListener(e -> {
            try {
                // 1. Recoger datos
                String nombre = form.getNombre();
                double stock = Double.parseDouble(form.getStock());
                double min = Double.parseDouble(form.getMinimo());
                double precio = Double.parseDouble(form.getPrecio());

                // 2. Crear objeto
                Product nuevoProd = new Product(0, nombre, stock, min, precio);

                // 3. Guardar en BD
                if (dao.guardar(nuevoProd)) {
                    form.showMessage("¡Producto guardado!");
                    form.dispose(); // Cerrar formulario
                    cargarDatos();  // ¡Refrescar la tabla automáticamente!
                } else {
                    form.showMessage("Error al guardar en base de datos.");
                }
            } catch (NumberFormatException ex) {
                form.showMessage("Error: Los campos numéricos no son válidos.");
            }
        });
        
        form.setVisible(true);
    }

    private void abrirFormularioEditar() {
        Integer productoId = listView.getProductoSeleccionadoId();
        if (productoId == null) {
            listView.showMessage("Selecciona un producto para editar.");
            return;
        }

        Product producto = dao.obtenerPorId(productoId);
        if (producto == null) {
            listView.showMessage("No se pudo cargar el producto seleccionado.");
            return;
        }

        ProductFormView form = new ProductFormView(listView);
        form.setProducto(
            producto.getId(),
            producto.getNombre(),
            producto.getStockActual(),
            producto.getStockMinimo(),
            producto.getPrecio()
        );

        form.addGuardarListener(e -> {
            try {
                String nombre = form.getNombre();
                double stock = Double.parseDouble(form.getStock());
                double min = Double.parseDouble(form.getMinimo());
                double precio = Double.parseDouble(form.getPrecio());

                Product actualizado = new Product(form.getProductId(), nombre, stock, min, precio);

                if (dao.actualizar(actualizado)) {
                    form.showMessage("¡Producto actualizado!");
                    form.dispose();
                    cargarDatos();
                } else {
                    form.showMessage("Error al actualizar en base de datos.");
                }
            } catch (NumberFormatException ex) {
                form.showMessage("Error: Los campos numéricos no son válidos.");
            }
        });

        form.setVisible(true);
    }
    
    /* 
    private void calcularValorInventario() {
        util.patterns.strategy.IStockStrategy estrategia = new util.patterns.strategy.SellPriceStrategy();
        
        // 2. Obtenemos datos
        List<Product> lista = dao.listarTodos();
        
        // 3. Ejecutamos estrategia
        double valor = estrategia.calcularValorTotal(lista);
        
        JOptionPane.showMessageDialog(view, 
            "El valor del almacén según [" + estrategia.getNombreEstrategia() + "] es:\n" + valor + " €");
    }
    */
    
}
