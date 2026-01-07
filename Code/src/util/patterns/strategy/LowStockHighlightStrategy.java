package util.patterns.strategy;

import javax.swing.JTable;
import util.patterns.decorator.StockBajoDecorator;

public class LowStockHighlightStrategy implements IStockViewStrategy {
    @Override
    public void apply(JTable table) {
        table.setDefaultRenderer(Object.class, new StockBajoDecorator());
    }
}
