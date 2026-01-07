package util.patterns.observer;

public interface IStockSubject {
    void attach(IStockObserver observer);
    void detach(IStockObserver observer);
    void notifyObservers(String nombre, double actual, double minimo);
}