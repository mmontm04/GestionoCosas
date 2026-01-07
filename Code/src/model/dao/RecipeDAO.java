package model.dao;

import config.DbConnection;
import model.entities.Recipe;
import model.entities.RecipeIngredient;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RecipeDAO implements IRecipeDAO {

    @Override
    public List<Recipe> listarTodas() {
        List<Recipe> lista = new ArrayList<>();
        String sql = "SELECT * FROM recetas";

        try (Connection conn = DbConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Recipe r = new Recipe();
                r.setId(rs.getInt("id"));
                r.setNombre(rs.getString("nombre"));
                r.setDescripcion(rs.getString("descripcion"));
                r.setPrecioVenta(rs.getDouble("precio_venta"));
                lista.add(r);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar recetas: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public boolean save(Recipe r) {
        String sqlReceta = "INSERT INTO recetas (nombre, descripcion, precio_venta) VALUES (?, ?, ?)";
        String sqlIngrediente = "INSERT INTO receta_ingredientes (receta_id, producto_id, cantidad_necesaria) VALUES (?, ?, ?)";
        
        Connection conn = null;
        
        try {
            conn = config.DbConnection.getInstance().getConnection();
            conn.setAutoCommit(false); // 1. INICIAR TRANSACCIÓN (Todo o nada)

            // A. Guardar la Receta
            PreparedStatement psReceta = conn.prepareStatement(sqlReceta, Statement.RETURN_GENERATED_KEYS);
            psReceta.setString(1, r.getNombre());
            psReceta.setString(2, r.getDescripcion());
            psReceta.setDouble(3, r.getPrecioVenta());
            psReceta.executeUpdate();

            // B. Obtener el ID generado (ej: ID 15)
            ResultSet rs = psReceta.getGeneratedKeys();
            int idReceta = 0;
            if (rs.next()) {
                idReceta = rs.getInt(1);
            }

            // C. Guardar los ingredientes uno a uno
            PreparedStatement psIngrediente = conn.prepareStatement(sqlIngrediente);
            for (RecipeIngredient item : r.getIngredientes()) {
                psIngrediente.setInt(1, idReceta); // Usamos el ID nuevo
                psIngrediente.setInt(2, item.getProducto().getId());
                psIngrediente.setDouble(3, item.getCantidad());
                psIngrediente.executeUpdate(); // Ejecutar inserción
            }

            conn.commit(); // 2. CONFIRMAR CAMBIOS (Si llegamos aquí, todo ha ido bien)
            return true;

        } catch (SQLException e) {
            System.err.println("Error al guardar receta compleja: " + e.getMessage());
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); } // DESHACER si hay error
            }
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        }
    }

    @Override
    public boolean update(Recipe r) {
        String sqlReceta = "UPDATE recetas SET nombre = ?, descripcion = ?, precio_venta = ? WHERE id = ?";
        String sqlDeleteIngredientes = "DELETE FROM receta_ingredientes WHERE receta_id = ?";
        String sqlIngrediente = "INSERT INTO receta_ingredientes (receta_id, producto_id, cantidad_necesaria) VALUES (?, ?, ?)";

        Connection conn = null;
        try {
            conn = DbConnection.getInstance().getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement psReceta = conn.prepareStatement(sqlReceta)) {
                psReceta.setString(1, r.getNombre());
                psReceta.setString(2, r.getDescripcion());
                psReceta.setDouble(3, r.getPrecioVenta());
                psReceta.setInt(4, r.getId());
                psReceta.executeUpdate();
            }

            try (PreparedStatement psDelete = conn.prepareStatement(sqlDeleteIngredientes)) {
                psDelete.setInt(1, r.getId());
                psDelete.executeUpdate();
            }

            try (PreparedStatement psIngrediente = conn.prepareStatement(sqlIngrediente)) {
                for (RecipeIngredient item : r.getIngredientes()) {
                    psIngrediente.setInt(1, r.getId());
                    psIngrediente.setInt(2, item.getProducto().getId());
                    psIngrediente.setDouble(3, item.getCantidad());
                    psIngrediente.executeUpdate();
                }
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al actualizar receta: " + e.getMessage());
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        }
    }

    @Override
    public Recipe getRecetaCompleta(int id) {
        Recipe r = null;
        String sqlReceta = "SELECT * FROM recetas WHERE id = ?";
        String sqlIng = "SELECT ri.cantidad_necesaria, p.id, p.nombre, p.stock_actual, p.precio " +
                        "FROM receta_ingredientes ri " +
                        "JOIN productos p ON ri.producto_id = p.id " +
                        "WHERE ri.receta_id = ?";

        try (Connection conn = DbConnection.getInstance().getConnection();
             PreparedStatement psR = conn.prepareStatement(sqlReceta)) {
            
            psR.setInt(1, id);
            ResultSet rs = psR.executeQuery();
            
            if (rs.next()) {
                r = new Recipe(rs.getInt("id"), rs.getString("nombre"), rs.getString("descripcion"), rs.getDouble("precio_venta"));
                
                // Cargar ingredientes
                try (PreparedStatement psI = conn.prepareStatement(sqlIng)) {
                    psI.setInt(1, id);
                    ResultSet rsI = psI.executeQuery();
                    while (rsI.next()) {
                        model.entities.Product p = new model.entities.Product(
                            rsI.getInt("id"), rsI.getString("nombre"), 
                            rsI.getDouble("stock_actual"), 0, rsI.getDouble("precio")
                        );
                        r.addIngrediente(p, rsI.getDouble("cantidad_necesaria"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return r;
    }

    @Override
    public boolean delete(int id) {
        String sqlDeleteIngredientes = "DELETE FROM receta_ingredientes WHERE receta_id = ?";
        String sqlDeleteReceta = "DELETE FROM recetas WHERE id = ?";

        Connection conn = null;
        try {
            conn = DbConnection.getInstance().getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement psIngredientes = conn.prepareStatement(sqlDeleteIngredientes)) {
                psIngredientes.setInt(1, id);
                psIngredientes.executeUpdate();
            }

            try (PreparedStatement psReceta = conn.prepareStatement(sqlDeleteReceta)) {
                psReceta.setInt(1, id);
                int filas = psReceta.executeUpdate();
                conn.commit();
                return filas > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al eliminar receta: " + e.getMessage());
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        }
    }
}
