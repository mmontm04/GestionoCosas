package model.service;

import config.DbConnection;
import model.dao.RecipeDAO;
import model.entities.Recipe;
import model.entities.RecipeIngredient;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ChefService {
    private RecipeDAO recetaDAO;

    public ChefService() {
        this.recetaDAO = new RecipeDAO();
    }

    public String cocinarReceta(Recipe receta) {
        Connection conn = null;
        try {
            conn = DbConnection.getInstance().getConnection();
            conn.setAutoCommit(false); // Inicio Transacción

            // Vamos a usar el SQL directo aquí para hacerlo en bloque
            String sqlUpdate = "UPDATE productos SET stock_actual = stock_actual - ? WHERE id = ? AND stock_actual >= ?";
            PreparedStatement pstmt = conn.prepareStatement(sqlUpdate);

            for (RecipeIngredient ing : receta.getIngredientes()) {
                pstmt.setDouble(1, ing.getCantidad());
                pstmt.setInt(2, ing.getProducto().getId());
                pstmt.setDouble(3, ing.getCantidad()); // Check de stock negativo
                
                int afectados = pstmt.executeUpdate();
                if (afectados == 0) {
                    conn.rollback();
                    return "Error: No hay suficiente stock de " + ing.getProducto().getNombre();
                }
            }

            conn.commit(); // Confirmar cambios
            return "OK";

        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) {}
            return "Error de base de datos: " + e.getMessage();
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); } catch (SQLException ex) {}
        }
    }
}