package controller.factory;

import controller.impl.*;

public class ControllerFactory {
    
    // Método Factory estático
    public static Object createController(String type) {
        switch (type) {
            case "PRODUCTO":
                return new ProductController();
            case "RECETA":
                return new RecipeController();
            case "USUARIO":
                return new UserController();
            case "COCINA":
                return new ChefController();
            /* case "INFORME":
                return new ReportController();  */
            default:
                return null;
        }
    }
}