package model.dao;

import config.DbConnection;
import model.entities.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO implements IProductDAO{

    @Override
    public List<Product> listarTodos() {
        List<Product> lista = new ArrayList<>();
        String sql = "SELECT * FROM productos";

        try (Connection conn = DbConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Product p = new Product();
                p.setId(rs.getInt("id"));
                p.setNombre(rs.getString("nombre"));
                p.setStockActual(rs.getDouble("stock_actual"));
                p.setStockMinimo(rs.getDouble("stock_minimo"));
                p.setPrecio(rs.getDouble("precio"));
                lista.add(p);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar productos: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public boolean guardar(Product p) {
        String sql = "INSERT INTO productos (nombre, stock_actual, stock_minimo, precio) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DbConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, p.getNombre());
            pstmt.setDouble(2, p.getStockActual());
            pstmt.setDouble(3, p.getStockMinimo());
            pstmt.setDouble(4, p.getPrecio());
            
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0; // Devuelve true si se guardó
            
        } catch (SQLException e) {
            System.err.println("Error al guardar producto: " + e.getMessage());
            return false;
        }
    }
    
    // Aquí añadiremos más adelante: crear, actualizar, eliminar...
}