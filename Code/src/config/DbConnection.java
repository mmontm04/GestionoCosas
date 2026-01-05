package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnection {
    // 1. Instancia estática única (Singleton)
    private static DbConnection instance;
    private Connection connection;

    // 2. Datos de conexión
    private static final String HOST = "sql7.freesqldatabase.com"; 
    private static final String PORT = "3306";
    private static final String DB_NAME = "sql7813457";
    private static final String USER = "sql7813457"; 
    private static final String PASSWORD = "nilm3AGUkD";

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