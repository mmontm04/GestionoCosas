package app;

import config.DbConnection;
import java.sql.Connection;
import javax.swing.SwingUtilities;

// Importaremos el controlador de login más adelante
// import controller.impl.LoginController;

public class Main {
    public static void main(String[] args) {
        // Ejecutar en el hilo de despacho de eventos de Swing (Buenas prácticas)
        SwingUtilities.invokeLater(() -> {
            System.out.println("--- Iniciando GestionoCosas ---");

            // 1. Probar la conexión a la Base de Datos
            Connection conn = DbConnection.getInstance().getConnection();

            if (conn != null) {
                System.out.println("¡El sistema está listo para arrancar!");
                
                // AQUÍ ARRANCARÁ TU APP REAL CUANDO TENGAMOS EL LOGIN:
                // LoginController loginController = ControllerFactory.createLoginController();
                // loginController.init();
                
            } else {
                System.err.println("FATAL: No se pudo conectar a la base de datos. La aplicación se cerrará.");
            }
        });
    }
}