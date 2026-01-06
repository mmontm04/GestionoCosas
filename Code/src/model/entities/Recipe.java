package model.entities;

import java.util.ArrayList;
import java.util.List;

public class Recipe {
    private int id;
    private String nombre;
    private String descripcion;
    private double precioVenta;
    private List<RecipeIngredient> ingredientes = new ArrayList<>();

    public Recipe() {}

    public Recipe(int id, String nombre, String descripcion, double precioVenta) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precioVenta = precioVenta;
    }

    // Getters
    public int getId() { 
        return id;
    }

    public String getNombre() { 
        return nombre;
    }

    public String getDescripcion() { 
        return descripcion;
    }

    public double getPrecioVenta() { 
        return precioVenta;
    }

    //Setters
    public void setId(int id) { 
        this.id = id;
    }    

    public void setNombre(String nombre) { 
        this.nombre = nombre;
    }    

    public void setDescripcion(String descripcion) { 
        this.descripcion = descripcion;
    }
    
    public void setPrecioVenta(double precioVenta) { 
        this.precioVenta = precioVenta;
    }

    public List<RecipeIngredient> getIngredientes() {
        return ingredientes;
    }

    public void addIngrediente(Product p, double cantidad) {
        this.ingredientes.add(new RecipeIngredient(p, cantidad));
    }

    @Override
    public String toString() { return nombre; }
}