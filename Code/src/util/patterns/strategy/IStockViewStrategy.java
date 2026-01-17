package util.patterns.strategy;

import javax.swing.JTable;

public interface IStockViewStrategy {
    // Cambiamos el método para que reciba la tabla entera y ella se encargue
    void apply(JTable table);
}