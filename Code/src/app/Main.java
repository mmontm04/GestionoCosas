package app;

import config.DbConnection;
import controller.impl.LoginController;
import java.sql.Connection;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // 1. Probar conexión silenciosamente
            Connection conn = DbConnection.getInstance().getConnection();
            
            if (conn != null) {
                // 2. Si hay conexión, lanzamos el Controlador del Login
                LoginController loginController = new LoginController();          
                loginController.init();
            } else {
                System.err.println("No se pudo conectar a la base de datos.");
            }
        });
    }
}