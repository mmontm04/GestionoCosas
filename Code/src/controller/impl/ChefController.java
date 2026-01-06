package controller.impl;

import model.dao.RecipeDAO;
import model.entities.Recipe;
import model.service.ChefService;
import util.SessionController;
import view.impl.RecipeListView;

import javax.swing.JOptionPane;
import java.util.List;

public class ChefController {
    private RecipeListView view;
    private RecipeDAO recetaDAO;
    private ChefService service;

    public ChefController() {
        this.view = new RecipeListView();
        this.recetaDAO = new RecipeDAO();
        this.service = new ChefService();
        
        // 1. Adaptar la vista para el Cocinero
        // Cambiamos el título y el texto del botón para que tenga sentido en este contexto
        this.view.setTitle("Cocina - Lista de Platos a Preparar");
        this.view.getBtnNuevo().setText("Cocinar Plato"); 
        
        // 2. Asignar la acción de "Cocinar" al botón
        this.view.getBtnNuevo().addActionListener(e -> cocinarPlatoSeleccionado());
    }

    public void init() {
        // --- BLOQUE DE SEGURIDAD ---
        // Permitimos entrar a COCINERO y a GERENTE (para supervisar)
        String rol = SessionController.getInstance().getUser().getRole();
        if (!rol.equals("COCINERO") && !rol.equals("GERENTE")) {
             JOptionPane.showMessageDialog(null, "Acceso denegado: Solo personal de cocina.");
             return;
        }
        
        // Si es Gerente, le avisamos de que está en modo "Cocina"
        if (rol.equals("GERENTE")) {
            this.view.setTitle("Cocina (Modo Supervisión Gerente)");
        }
        // ---------------------------

        cargarDatos();
        view.setVisible(true);
    }

    private void cargarDatos() {
        // Cargar la lista de recetas disponibles para cocinar
        List<Recipe> recetas = recetaDAO.listarTodas();
        view.mostrarRecetas(recetas);
    }

    private void cocinarPlatoSeleccionado() {
        // 1. Obtener el ID de la receta seleccionada en la tabla
        int idReceta = view.getIdRecetaSeleccionada();
        
        if (idReceta == -1) {
            JOptionPane.showMessageDialog(view, "Por favor, selecciona un plato de la tabla primero.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Confirmación de seguridad
        int confirm = JOptionPane.showConfirmDialog(view, 
            "¿Seguro que quieres cocinar este plato?\nSe descontarán los ingredientes del stock.", 
            "Confirmar Cocinado", 
            JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        Recipe recetaCompleta = recetaDAO.getRecetaCompleta(idReceta);

        if (recetaCompleta == null) {
            JOptionPane.showMessageDialog(view, "Error: No se pudo cargar la información de la receta.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String resultado = service.cocinarReceta(recetaCompleta);

        // 5. Mostrar resultado
        if (resultado.equals("OK")) {
            JOptionPane.showMessageDialog(view, "✅ ¡Plato cocinado con éxito!\nStock de ingredientes actualizado.", "Cocina", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(view, "❌ No se pudo cocinar:\n" + resultado, "Error de Stock", JOptionPane.ERROR_MESSAGE);
        }
    }
}