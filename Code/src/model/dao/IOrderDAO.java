package model.dao;

import java.util.List;
import java.util.Map;

import model.entities.Order;
import model.entities.Product;

public interface IOrderDAO {
    boolean crearPedido(Order p);
    boolean marcarRecibido(int idPedido);
    List<Order> listarTodos();
    Order obtenerPedidoCompleto(int id);
    boolean procesarEntregaParcial(Order pedidoOriginal, Map<Product, Double> cantidadesRecibidas);
    boolean actualizarCantidades(Order pedido);   
}
