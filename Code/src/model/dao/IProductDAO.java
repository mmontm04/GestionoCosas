package model.dao;

import model.entities.Product;
import java.util.List;

public interface IProductDAO {
    List<Product> listarTodos();
    boolean guardar(Product p);
    boolean actualizar(Product p);
    boolean eliminar(int id);
    boolean actualizarStock(int productoId, double cantidadARestar);
    Product obtenerPorId(int id);
    List<Product> listarPaginado(int limite, int offset);
    int contarTotal();
}
