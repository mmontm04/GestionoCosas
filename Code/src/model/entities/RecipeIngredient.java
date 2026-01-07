package model.entities;

public class RecipeIngredient {
    private Product producto;
    private double cantidad;

    public RecipeIngredient(Product producto, double cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;
    }

    public Product getProducto() { return producto; }
    public double getCantidad() { return cantidad; }
    
    // Para mostrarlo bonito en la tabla luego
    @Override
    public String toString() {
        return producto.getNombre() + " (" + cantidad + ")";
    }
}