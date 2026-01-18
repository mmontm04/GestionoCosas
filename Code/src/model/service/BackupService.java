package model.service;

import config.DbConnection;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.Date;

public class BackupService {

    // --- OPCIÓN SQL ---
    public void exportarSQL(String rutaArchivo) throws Exception {
        StringBuilder sql = new StringBuilder();
        sql.append("-- BACKUP SYSTEM - ").append(new Date()).append("\nUSE test;\n\n");
        
        String[] tablas = {"usuarios", "productos", "recetas", "receta_ingredientes", "pedidos", "lineas_pedido"};

        try (Connection conn = DbConnection.getInstance().getConnection()) {
            for (String tabla : tablas) {
                try {
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT * FROM " + tabla);
                    ResultSetMetaData meta = rs.getMetaData();
                    int numCols = meta.getColumnCount();

                    sql.append("-- Tabla: ").append(tabla).append("\n");
                    while (rs.next()) {
                        sql.append("INSERT INTO ").append(tabla).append(" VALUES (");
                        for (int i = 1; i <= numCols; i++) {
                            Object val = rs.getObject(i);
                            if (val == null) sql.append("NULL");
                            else if (val instanceof Number) sql.append(val);
                            else sql.append("'").append(val.toString().replace("'", "''")).append("'");
                            
                            if (i < numCols) sql.append(", ");
                        }
                        sql.append(");\n");
                    }
                    sql.append("\n");
                } catch (SQLException e) {
                    System.err.println("Tabla no encontrada o vacía: " + tabla);
                }
            }
        }
        guardar(rutaArchivo, sql.toString());
    }

    // --- OPCIÓN CSV ---
    public void exportarCSV(String tabla, String rutaArchivo) throws Exception {
        StringBuilder csv = new StringBuilder();
        try (Connection conn = DbConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM " + tabla)) {

            int cols = rs.getMetaData().getColumnCount();
            // Cabeceras
            for(int i=1; i<=cols; i++) csv.append(rs.getMetaData().getColumnName(i)).append(i<cols?",":"\n");
            // Datos
            while(rs.next()) {
                for(int i=1; i<=cols; i++) {
                    String val = rs.getString(i);
                    if(val != null && val.contains(",")) val = "\"" + val + "\""; // Proteger comas
                    csv.append(val==null?"":val).append(i<cols?",":"\n");
                }
            }
        }
        guardar(rutaArchivo, csv.toString());
    }

    // --- OPCIÓN JSON ---
    public void exportarJSON(String tabla, String rutaArchivo) throws Exception {
        StringBuilder json = new StringBuilder("[\n");
        try (Connection conn = DbConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM " + tabla)) {

            ResultSetMetaData meta = rs.getMetaData();
            int cols = meta.getColumnCount();
            boolean primero = true;

            while(rs.next()) {
                if(!primero) json.append(",\n");
                json.append("  {");
                for(int i=1; i<=cols; i++) {
                    String key = meta.getColumnName(i);
                    Object val = rs.getObject(i);
                    json.append("\"").append(key).append("\": ");
                    if(val instanceof Number) json.append(val);
                    else json.append("\"").append(val == null ? "" : val).append("\"");
                    if(i<cols) json.append(", ");
                }
                json.append("}");
                primero = false;
            }
            json.append("\n]");
        }
        guardar(rutaArchivo, json.toString());
    }

    private void guardar(String ruta, String contenido) throws IOException {
        try (FileWriter fw = new FileWriter(ruta)) { fw.write(contenido); }
    }
}