package model.entities;

public class Product {
    private int id;
    private String nombre;
    private double stockActual;
    private double stockMinimo;
    private double precio;

    public Product() {}

    public Product(int id, String nombre, double stockActual, double stockMinimo, double precio) {
        this.id = id;
        this.nombre = nombre;
        this.stockActual = stockActual;
        this.stockMinimo = stockMinimo;
        this.precio = precio;
    }

    // Getters
    public int getId() { 
        return id;
    }

    public String getNombre() { 
        return nombre;
    }

    public double getStockActual() { 
        return stockActual;
    }

    public double getStockMinimo() { 
        return stockMinimo;
    }

    public double getPrecio() { 
        return precio;
    }

    // Setters
    public void setId(int id) { 
        this.id = id;
    }

    public void setNombre(String nombre) { 
        this.nombre = nombre;
    }

    public void setStockActual(double stockActual) { 
        this.stockActual = stockActual;
    }

    public void setStockMinimo(double stockMinimo) { 
        this.stockMinimo = stockMinimo;
    }

    public void setPrecio(double precio) { 
        this.precio = precio;
    }
    
    // Para mostrarlo bonito en los combos si hace falta
    @Override
    public String toString() { 
        return nombre; 
    }
}