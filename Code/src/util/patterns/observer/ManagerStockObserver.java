package util.patterns.observer;

import javax.swing.JOptionPane;

public class ManagerStockObserver implements IStockObserver {
    @Override
    public void onStockBajo(String nombre, double actual, double minimo) {
        JOptionPane.showMessageDialog(null, 
            "⚠️ ALERTA DE STOCK: " + nombre + "\nQuedan: " + actual + " (Mínimo: " + minimo + ")", 
            "Atención Gerente", 
            JOptionPane.WARNING_MESSAGE);
    }
}