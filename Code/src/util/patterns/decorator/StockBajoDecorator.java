package util.patterns.decorator;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class StockBajoDecorator extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, 
            boolean isSelected, boolean hasFocus, int row, int column) {
        
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        try {
            // Obtenemos stock actual (columna 2) y mínimo (columna 3)
            double stock = Double.parseDouble(table.getValueAt(row, 2).toString());
            double minimo = Double.parseDouble(table.getValueAt(row, 3).toString());

            if (stock <= minimo) {
                c.setBackground(Color.RED);
                c.setForeground(Color.WHITE);
            } else {
                // Restaurar colores por defecto si no es bajo stock
                c.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
                c.setForeground(isSelected ? table.getSelectionForeground() : Color.BLACK);
            }
        } catch (Exception e) {
            // Ignorar errores de parseo
        }
        return c;
    }
}