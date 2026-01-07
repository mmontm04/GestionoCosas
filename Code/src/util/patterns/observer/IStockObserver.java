package util.patterns.observer;

public interface IStockObserver {
    void onStockBajo(String nombreProducto, double stockActual, double stockMinimo);
}