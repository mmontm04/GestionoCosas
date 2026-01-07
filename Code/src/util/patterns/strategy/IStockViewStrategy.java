package util.patterns.strategy;

import javax.swing.JTable;

public interface IStockViewStrategy {
    void apply(JTable table);
}
