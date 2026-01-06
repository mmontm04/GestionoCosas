package model.dao;

import model.entities.Product;
import java.util.List;

public interface IProductDAO {
    // Métodos que ya hemos implementado
    List<Product> listarTodos();
    boolean guardar(Product p);
    
    // Métodos futuros (puedes dejarlos comentados o declararlos ya)
    // boolean actualizar(Producto p);
    // boolean eliminar(int id);
    // Producto buscarPorId(int id);
}