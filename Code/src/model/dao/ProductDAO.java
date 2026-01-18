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
        String sql = "SELECT * FROM productos WHERE activo = 1";

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
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al guardar producto: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean actualizar(Product p) {
        String sql = "UPDATE productos SET nombre = ?, stock_actual = ?, stock_minimo = ?, precio = ? WHERE id = ?";

        try (Connection conn = DbConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, p.getNombre());
            pstmt.setDouble(2, p.getStockActual());
            pstmt.setDouble(3, p.getStockMinimo());
            pstmt.setDouble(4, p.getPrecio());
            pstmt.setInt(5, p.getId());

            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar producto: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminar(int id) {
        String sql = "UPDATE productos SET activo = 0 WHERE id = ?";

        try (Connection conn = DbConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar producto (Lógico): " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean actualizarStock(int productoId, double cantidadARestar) {
        String sql = "UPDATE productos SET stock_actual = stock_actual - ? WHERE id = ? AND stock_actual >= ?";
        
        try (java.sql.Connection conn = config.DbConnection.getInstance().getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDouble(1, cantidadARestar);
            pstmt.setInt(2, productoId);
            pstmt.setDouble(3, cantidadARestar); // Para evitar stock negativo
            
            int filas = pstmt.executeUpdate();
            
            if (filas > 0) {
                Product p = obtenerPorId(productoId);
                
                if (p != null) {
                    util.patterns.observer.StockAlertManager.getInstance()
                        .comprobarStock(p.getNombre(), p.getStockActual(), p.getStockMinimo());
                }
                return true; 
            }
            return false; 
            
        } catch (java.sql.SQLException e) {
            System.err.println("Error al actualizar stock: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Product obtenerPorId(int id) {
        String sql = "SELECT * FROM productos WHERE id = ?";
        Product p = null;
        
        try (java.sql.Connection conn = config.DbConnection.getInstance().getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            java.sql.ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                p = new Product();
                p.setId(rs.getInt("id"));
                p.setNombre(rs.getString("nombre"));
                p.setStockActual(rs.getDouble("stock_actual"));
                p.setStockMinimo(rs.getDouble("stock_minimo"));
                p.setPrecio(rs.getDouble("precio"));
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Error al obtener producto por ID: " + e.getMessage());
        }
        return p;
    }

    @Override
    public int contarTotal() {
        String sql = "SELECT COUNT(*) FROM productos WHERE activo = 1";
        try (java.sql.Connection conn = config.DbConnection.getInstance().getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql);
             java.sql.ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public java.util.List<Product> listarPaginado(int limite, int offset) {
        java.util.List<Product> lista = new java.util.ArrayList<>();
        String sql = "SELECT * FROM productos WHERE activo = 1 LIMIT ? OFFSET ?";

        try (java.sql.Connection conn = config.DbConnection.getInstance().getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, limite);
            pstmt.setInt(2, offset);
            
            java.sql.ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Product p = new Product();
                p.setId(rs.getInt("id"));
                p.setNombre(rs.getString("nombre"));
                p.setStockActual(rs.getDouble("stock_actual"));
                p.setStockMinimo(rs.getDouble("stock_minimo"));
                p.setPrecio(rs.getDouble("precio"));
                lista.add(p);
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}
