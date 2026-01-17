package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnection {
    // 1. Instancia estática única (Singleton)
    private static DbConnection instance;
    private Connection connection;

    // 2. Datos de conexión
    private static final String HOST = "gateway01.eu-central-1.prod.aws.tidbcloud.com"; 
    private static final String PORT = "4000";
    private static final String DB_NAME = "test";
    private static final String USER = "3WL8FgGBaWqa2sN.root";
    private static final String PASSWORD = "wepSlW45oiVQtItH";

    // Cadena de conexión JDBC
    private static final String URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DB_NAME;

    // 3. Constructor privado para evitar que se haga 'new DbConnection()' desde fuera
    private DbConnection() {
        try {
            // Cargar el driver explícitamente (opcional en versiones nuevas, pero recomendable)
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Conexión a la base de datos establecida con éxito.");
            
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Error: No se encontró el Driver de MySQL.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("❌ Error al conectar a la base de datos.");
            e.printStackTrace();
        }
    }

    // 4. Método estático para obtener la instancia
    public static DbConnection getInstance() {
        try {
            if (instance == null || instance.getConnection().isClosed()) {
                instance = new DbConnection();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return instance;
    }

    // 5. Método para obtener el objeto Connection real
    public Connection getConnection() {
        return connection;
    }
}