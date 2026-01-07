package util.patterns.strategy;
import model.entities.Product;
import java.util.List;

public interface IStockStrategy {
    double calcularValorTotal(List<Product> productos);
    String getNombreEstrategia();
}