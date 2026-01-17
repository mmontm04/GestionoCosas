package util.patterns.strategy;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class LowStockHighlightStrategy implements IStockViewStrategy {

    @Override
    public void apply(JTable table) {
        // Definimos un Renderizado personalizado y se lo asignamos a la tabla
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                // 2. Lógica de Stock Bajo
                try {
                    Object stockObj = table.getValueAt(row, 2);
                    Object minObj = table.getValueAt(row, 3);

                    if (stockObj != null && minObj != null) {
                        double stock = Double.parseDouble(stockObj.toString());
                        double min = Double.parseDouble(minObj.toString());

                        if (stock <= min) {
                            c.setBackground(new Color(255, 100, 100)); // Rojo suave
                            c.setForeground(Color.WHITE);
                            return c; // Salimos ya pintados
                        }
                    }
                } catch (Exception e) {
                    // Si hay error leyendo números, ignoramos
                }

                if (isSelected) {
                    c.setBackground(table.getSelectionBackground());
                    c.setForeground(table.getSelectionForeground());
                } else {
                    c.setBackground(Color.WHITE);
                    c.setForeground(Color.BLACK);
                }

                return c;
            }
        });
    }
}