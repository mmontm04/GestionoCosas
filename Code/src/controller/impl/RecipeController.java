package controller.impl;

import util.SessionController;
import model.dao.IRecipeDAO;
import model.dao.RecipeDAO;
import model.entities.Recipe;
import view.impl.ManagerRecipesFormView;
import view.impl.RecipeListView;

import javax.swing.JOptionPane;
import java.util.List;

public class RecipeController {
    private RecipeListView view;
    private IRecipeDAO dao;

    public RecipeController() {
        this.view = new RecipeListView();
        this.dao = new RecipeDAO();
        
        // Evento botón nuevo
        this.view.getBtnNuevo().addActionListener(e -> mostrarFormulario());
    }

    public void init() {
        cargarDatos();

        boolean esJefe = util.SessionController.getInstance().esGerente();
        view.configurarSeguridad(esJefe);

        view.setVisible(true);
    }

    private void cargarDatos() {
        List<Recipe> recetas = dao.listarTodas();
        view.mostrarRecetas(recetas);
    }

    private void mostrarFormulario() {
        if (!util.SessionController.getInstance().esGerente()) {
            // ... (tu bloqueo de seguridad) ...
            return;
        }

        // 1. Cargar productos para el combo
        model.dao.ProductDAO prodDao = new model.dao.ProductDAO();
        List<model.entities.Product> listaProductos = prodDao.listarTodos();

        // 2. Crear vista pasando productos
        view.impl.ManagerRecipesFormView form = new view.impl.ManagerRecipesFormView(view, listaProductos);
        
        form.addGuardarListener(e -> {
            try {
                // Recoger datos básicos
                model.entities.Recipe r = new model.entities.Recipe();
                r.setNombre(form.getNombre());
                r.setDescripcion(form.getDescripcion());
                r.setPrecioVenta(Double.parseDouble(form.getPrecio()));

                // Recoger ingredientes de la lista temporal
                for (model.entities.RecipeIngredient ri : form.getIngredientesSeleccionados()) {
                    r.addIngrediente(ri.getProducto(), ri.getCantidad());
                }

                // Guardar (DAO se encarga de todo)
                if (dao.save(r)) {
                    JOptionPane.showMessageDialog(form, "¡Receta guardada con sus ingredientes!");
                    form.dispose();
                    cargarDatos(); // Refrescar lista
                } else {
                    JOptionPane.showMessageDialog(form, "Error al guardar.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(form, "Error en los datos: " + ex.getMessage());
            }
        });
        
        form.setVisible(true);
    }
}