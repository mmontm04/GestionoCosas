package model.dao;

import config.DbConnection;
import model.entities.Order;
import model.entities.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrderDAO {

    public boolean crearPedido(Order p) {
        Connection conn = null;
        try {
            conn = DbConnection.getInstance().getConnection();
            conn.setAutoCommit(false); 

            // Insertar Cabecera
            String sqlHead = "INSERT INTO pedidos (proveedor, estado) VALUES (?, 'PENDIENTE')";
            PreparedStatement psHead = conn.prepareStatement(sqlHead, Statement.RETURN_GENERATED_KEYS);
            psHead.setString(1, p.getProveedor());
            psHead.executeUpdate();

            ResultSet rs = psHead.getGeneratedKeys();
            int idPedido = 0;
            if (rs.next()) idPedido = rs.getInt(1);

            // Insertar Líneas
            String sqlLine = "INSERT INTO lineas_pedido (pedido_id, producto_id, cantidad) VALUES (?, ?, ?)";
            PreparedStatement psLine = conn.prepareStatement(sqlLine);
            
            for (Map.Entry<Product, Double> entry : p.getItems().entrySet()) {
                psLine.setInt(1, idPedido);
                psLine.setInt(2, entry.getKey().getId());
                psLine.setDouble(3, entry.getValue());
                psLine.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) {}
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); } catch (SQLException ex) {}
        }
    }

    public boolean marcarRecibido(int idPedido) {
        Connection conn = null;
        try {
            conn = DbConnection.getInstance().getConnection();
            conn.setAutoCommit(false);

            PreparedStatement check = conn.prepareStatement("SELECT estado FROM pedidos WHERE id = ?");
            check.setInt(1, idPedido);
            ResultSet rs = check.executeQuery();
            if (rs.next() && "RECIBIDO".equals(rs.getString("estado"))) {
                return false; 
            }

            String sqlItems = "SELECT producto_id, cantidad FROM lineas_pedido WHERE pedido_id = ?";
            PreparedStatement psItems = conn.prepareStatement(sqlItems);
            psItems.setInt(1, idPedido);
            ResultSet rsItems = psItems.executeQuery();

            String sqlUpdateStock = "UPDATE productos SET stock_actual = stock_actual + ? WHERE id = ?";
            PreparedStatement psUpdate = conn.prepareStatement(sqlUpdateStock);

            while (rsItems.next()) {
                psUpdate.setDouble(1, rsItems.getDouble("cantidad")); 
                psUpdate.setInt(2, rsItems.getInt("producto_id"));
                psUpdate.executeUpdate();
            }

            PreparedStatement psEstado = conn.prepareStatement("UPDATE pedidos SET estado = 'RECIBIDO' WHERE id = ?");
            psEstado.setInt(1, idPedido);
            psEstado.executeUpdate();

            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) {}
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); } catch (SQLException ex) {}
        }
    }
    
    public List<Order> listarTodos() {
        List<Order> lista = new ArrayList<>();
        String sql = "SELECT * FROM pedidos ORDER BY id DESC";
        
        try (Connection conn = DbConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                // Recuperamos la fecha como String
                String fechaStr = "";
                Timestamp ts = rs.getTimestamp("fecha");
                if (ts != null) {
                    fechaStr = ts.toString(); // Convierte el timestamp a texto legible
                }

                lista.add(new Order(
                    rs.getInt("id"), 
                    fechaStr, 
                    rs.getString("proveedor"), 
                    rs.getString("estado")
                ));
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return lista;
    }
}