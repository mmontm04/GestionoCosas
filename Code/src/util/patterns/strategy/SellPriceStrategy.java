package util.patterns.strategy;
import model.entities.Product;
import java.util.List;

public class SellPriceStrategy implements IStockStrategy {
    @Override
    public double calcularValorTotal(List<Product> productos) {
        double total = 0;
        for (Product p : productos) {
            total += (p.getStockActual() * p.getPrecio());
        }
        return total;
    }

    @Override
    public String getNombreEstrategia() {
        return "Valoración a Precio de Venta (PVP)";
    }
}