package controller.impl;

import util.SessionController;
import view.ManagerRecipesFormView;
import view.RecipeListView;
import model.dao.IRecipeDAO;
import model.dao.RecipeDAO;
import model.entities.Recipe;

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
        this.view.getBtnEditar().addActionListener(e -> editarRecetaSeleccionada());
        this.view.getBtnEliminar().addActionListener(e -> eliminarRecetaSeleccionada());
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
            return;
        }

        model.dao.ProductDAO prodDao = new model.dao.ProductDAO();
        List<model.entities.Product> listaProductos = prodDao.listarTodos();

        view.ManagerRecipesFormView form = new view.ManagerRecipesFormView(view, listaProductos);
        
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

    private void editarRecetaSeleccionada() {
        if (!SessionController.getInstance().esGerente()) {
            return;
        }

        int recetaId = view.getIdRecetaSeleccionada();
        if (recetaId == -1) {
            JOptionPane.showMessageDialog(view, "Selecciona una receta para editar.");
            return;
        }

        Recipe receta = dao.getRecetaCompleta(recetaId);
        if (receta == null) {
            JOptionPane.showMessageDialog(view, "No se pudo cargar la receta seleccionada.");
            return;
        }

        model.dao.ProductDAO prodDao = new model.dao.ProductDAO();
        List<model.entities.Product> listaProductos = prodDao.listarTodos();

        ManagerRecipesFormView form = new ManagerRecipesFormView(view, listaProductos);
        form.setReceta(receta);
        form.setGuardarButtonText("ACTUALIZAR RECETA");

        form.addGuardarListener(e -> {
            try {
                Recipe actualizada = new Recipe();
                actualizada.setId(recetaId);
                actualizada.setNombre(form.getNombre());
                actualizada.setDescripcion(form.getDescripcion());
                actualizada.setPrecioVenta(Double.parseDouble(form.getPrecio()));

                for (model.entities.RecipeIngredient ri : form.getIngredientesSeleccionados()) {
                    actualizada.addIngrediente(ri.getProducto(), ri.getCantidad());
                }

                if (dao.update(actualizada)) {
                    JOptionPane.showMessageDialog(form, "¡Receta actualizada!");
                    form.dispose();
                    cargarDatos();
                } else {
                    JOptionPane.showMessageDialog(form, "Error al actualizar la receta.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(form, "Error en los datos: " + ex.getMessage());
            }
        });

        form.setVisible(true);
    }

    private void eliminarRecetaSeleccionada() {
        if (!SessionController.getInstance().esGerente()) {
            return;
        }

        int recetaId = view.getIdRecetaSeleccionada();
        if (recetaId == -1) {
            JOptionPane.showMessageDialog(view, "Selecciona una receta para eliminar.");
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(
            view,
            "¿Seguro que deseas eliminar la receta seleccionada?",
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION
        );

        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        if (dao.delete(recetaId)) {
            JOptionPane.showMessageDialog(view, "Receta eliminada.");
            cargarDatos();
        } else {
            JOptionPane.showMessageDialog(view, "No se pudo eliminar la receta.");
        }
    }
}
