package controller.impl;

import model.service.BackupService;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

public class BackupController {
    private BackupService service;

    public BackupController() {
        this.service = new BackupService();
    }

    // Método que llama la vista principal
    public void init() {
        // 1. Preguntar qué quiere hacer
        String[] opciones = {"Copia Seguridad (SQL)", "Exportar Productos (CSV)", "Exportar Pedidos (JSON)", "Cancelar"};
        
        int seleccion = JOptionPane.showOptionDialog(null, 
            "Seleccione formato de exportación:", "Gestión de Datos",
            JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, 
            null, opciones, opciones[0]);

        if (seleccion < 0 || seleccion == 3) return; // Cancelado

        // 2. Configurar extensiones
        String ext = (seleccion == 0) ? "sql" : (seleccion == 1) ? "csv" : "json";
        String desc = (seleccion == 0) ? "SQL Backup" : (seleccion == 1) ? "CSV Excel" : "JSON Data";
        String defaultName = (seleccion == 0) ? "backup_full" : (seleccion == 1) ? "productos" : "pedidos";

        // 3. Elegir dónde guardar
        JFileChooser ch = new JFileChooser();
        ch.setSelectedFile(new File(defaultName + "." + ext));
        ch.setFileFilter(new FileNameExtensionFilter(desc, ext));

        if (ch.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            String ruta = ch.getSelectedFile().getAbsolutePath();
            if (!ruta.endsWith("." + ext)) ruta += "." + ext;

            try {
                // 4. Ejecutar la lógica
                switch (seleccion) {
                    case 0: service.exportarSQL(ruta); break;
                    case 1: service.exportarCSV("productos", ruta); break;
                    case 2: service.exportarJSON("pedidos", ruta); break;
                }
                JOptionPane.showMessageDialog(null, "¡Exportación completada con éxito!");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
            }
        }
    }
}