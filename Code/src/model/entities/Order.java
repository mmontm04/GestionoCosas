package model.entities;
import java.util.HashMap;
import java.util.Map;

public class Order {
    private int id;
    private String fecha;
    private String proveedor;
    private String estado;
    private Map<Product, Double> items = new HashMap<>();

    public Order() {}
    
    // Constructor completo
    public Order(int id, String fecha, String proveedor, String estado) {
        this.id = id;
        this.fecha = fecha;
        this.proveedor = proveedor;
        this.estado = estado;
    }

    public void addItem(Product p, double cantidad) {
        items.put(p, cantidad);
    }

    // Getters 
    public int getId() { 
        return id;
    }

    public String getProveedor() { 
        return proveedor;
    }

    public String getEstado() { 
        return estado;
    }

    public String getFecha() { 
        return fecha;
    }

    public Map<Product, Double> getItems() { 
        return items;
    }
    
    //Setters
    public void setId(int id) { 
        this.id = id;
    }

    public void setProveedor(String proveedor) { 
        this.proveedor = proveedor;
    }

    public void setEstado(String estado) { 
        this.estado = estado;
    }

    public void setFecha(String fecha) { 
        this.fecha = fecha;
    }
}