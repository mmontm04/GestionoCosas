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

    public Order obtenerPedidoCompleto(int id) {
        Order o = null;
        String sqlHead = "SELECT * FROM pedidos WHERE id = ?";
        String sqlLines = "SELECT lp.cantidad, p.id, p.nombre, p.stock_actual, p.precio " +
                          "FROM lineas_pedido lp " +
                          "JOIN productos p ON lp.producto_id = p.id " +
                          "WHERE lp.pedido_id = ?";
                          
        try (Connection conn = DbConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlHead)) {
            
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                o = new Order(rs.getInt("id"), rs.getString("fecha"), rs.getString("proveedor"), rs.getString("estado"));
                
                // Cargar líneas
                try (PreparedStatement psL = conn.prepareStatement(sqlLines)) {
                    psL.setInt(1, id);
                    ResultSet rsL = psL.executeQuery();
                    while (rsL.next()) {
                        Product p = new Product(
                            rsL.getInt("id"), rsL.getString("nombre"), 
                            rsL.getDouble("stock_actual"), 0, rsL.getDouble("precio")
                        );
                        o.addItem(p, rsL.getDouble("cantidad"));
                    }
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return o;
    }

    public boolean procesarEntregaParcial(Order pedidoOriginal, Map<Product, Double> cantidadesRecibidas) {
        Connection conn = null;
        try {
            conn = DbConnection.getInstance().getConnection();
            conn.setAutoCommit(false);

            PreparedStatement psUpdateEstado = conn.prepareStatement("UPDATE pedidos SET estado = 'PARCIAL' WHERE id = ?");
            psUpdateEstado.setInt(1, pedidoOriginal.getId());
            psUpdateEstado.executeUpdate();

            String sqlUpdateLinea = "UPDATE lineas_pedido SET cantidad = ? WHERE pedido_id = ? AND producto_id = ?";
            String sqlUpdateStock = "UPDATE productos SET stock_actual = stock_actual + ? WHERE id = ?";
            
            PreparedStatement psLinea = conn.prepareStatement(sqlUpdateLinea);
            PreparedStatement psStock = conn.prepareStatement(sqlUpdateStock);

            for (Map.Entry<Product, Double> entry : cantidadesRecibidas.entrySet()) {
                Product prod = entry.getKey();
                Double cantRecibida = entry.getValue();

                // Actualizar línea del pedido original a lo que realmente llegó
                psLinea.setDouble(1, cantRecibida);
                psLinea.setInt(2, pedidoOriginal.getId());
                psLinea.setInt(3, prod.getId());
                psLinea.executeUpdate();

                // Sumar stock
                psStock.setDouble(1, cantRecibida);
                psStock.setInt(2, prod.getId());
                psStock.executeUpdate();
            }

            boolean hayResto = false;
            Order pedidoRestante = new Order();

            String nombreBase = pedidoOriginal.getProveedor();
            
            if (nombreBase.contains(" (RESTO")) {
                nombreBase = nombreBase.substring(0, nombreBase.indexOf(" (RESTO"));
            }
            
            pedidoRestante.setProveedor(nombreBase + " (RESTO #" + pedidoOriginal.getId() + ")");
            
            for (Map.Entry<Product, Double> entry : pedidoOriginal.getItems().entrySet()) {
                Product p = entry.getKey();
                Double cantOriginal = entry.getValue();
                Double cantRecibida = cantidadesRecibidas.getOrDefault(p, 0.0);
                Double cantFaltante = cantOriginal - cantRecibida;

                if (cantFaltante > 0) {
                    pedidoRestante.addItem(p, cantFaltante);
                    hayResto = true;
                }
            }

            if (hayResto) {
                // Insertar cabecera nuevo pedido
                String sqlNewHead = "INSERT INTO pedidos (proveedor, estado) VALUES (?, 'PENDIENTE')";
                PreparedStatement psNew = conn.prepareStatement(sqlNewHead, Statement.RETURN_GENERATED_KEYS);
                psNew.setString(1, pedidoRestante.getProveedor());
                psNew.executeUpdate();
                
                ResultSet rsK = psNew.getGeneratedKeys();
                int idNuevo = 0;
                if (rsK.next()) idNuevo = rsK.getInt(1);

                // Insertar líneas del resto
                String sqlNewLine = "INSERT INTO lineas_pedido (pedido_id, producto_id, cantidad) VALUES (?, ?, ?)";
                PreparedStatement psNL = conn.prepareStatement(sqlNewLine);
                for (Map.Entry<Product, Double> item : pedidoRestante.getItems().entrySet()) {
                    psNL.setInt(1, idNuevo);
                    psNL.setInt(2, item.getKey().getId());
                    psNL.setDouble(3, item.getValue());
                    psNL.executeUpdate();
                }
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

    public boolean actualizarCantidades(Order pedido) {
        String sql = "UPDATE lineas_pedido SET cantidad = ? WHERE pedido_id = ? AND producto_id = ?";
        Connection conn = null;
        try {
            conn = DbConnection.getInstance().getConnection();
            conn.setAutoCommit(false);
            
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (Map.Entry<Product, Double> entry : pedido.getItems().entrySet()) {
                    ps.setDouble(1, entry.getValue()); // Nueva cantidad
                    ps.setInt(2, pedido.getId());
                    ps.setInt(3, entry.getKey().getId());
                    ps.addBatch(); // Añadir al lote
                }
                ps.executeBatch(); // Ejecutar todos los updates
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
}