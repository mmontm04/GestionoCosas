package util.patterns.observer;

import java.util.ArrayList;
import java.util.List;

public class StockAlertManager implements IStockSubject {
    private static StockAlertManager instance;
    private List<IStockObserver> observers = new ArrayList<>();

    private StockAlertManager() {}

    public static StockAlertManager getInstance() {
        if (instance == null) instance = new StockAlertManager();
        return instance;
    }

    @Override
    public void attach(IStockObserver observer) {
        observers.add(observer);
    }

    @Override
    public void detach(IStockObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(String nombre, double actual, double minimo) {
        for (IStockObserver obs : observers) {
            obs.onStockBajo(nombre, actual, minimo);
        }
    }

    // Método que llamará el DAO para comprobar
    public void comprobarStock(String nombreProducto, double stockActual, double stockMinimo) {
        if (stockActual <= stockMinimo) {
            notifyObservers(nombreProducto, stockActual, stockMinimo);
        }
    }
}