package view.impl;

import controller.impl.ProductController;
import controller.impl.RecipeController;
import controller.impl.UserController;
import controller.impl.ChefController;
import controller.impl.BackupController;
import javax.swing.*;
import java.awt.*;

public class MainDashboardView extends JFrame {

    public MainDashboardView(String rol) {
        String rolLimpio = (rol != null) ? rol.trim().toUpperCase() : "";

        setTitle("Sistema de Gestión - Rol Detectado: [" + rolLimpio + "]");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        setLayout(new GridLayout(0, 1, 10, 10));

        JLabel lblBienvenida = new JLabel("Bienvenido (" + rolLimpio + ")", SwingConstants.CENTER);
        lblBienvenida.setFont(new Font("Arial", Font.BOLD, 20));
        add(lblBienvenida);

        if ("GERENTE".equals(rolLimpio)) {
            // ===============================================
            // ZONA EXCLUSIVA DE GERENTES
            // ===============================================
            JButton btnProductos = new JButton("1. Gestionar Productos");
            add(btnProductos);
            btnProductos.addActionListener(e -> new ProductController().init());

            JButton btnRecetas = new JButton("2. Gestión de Recetas");
            add(btnRecetas);
            btnRecetas.addActionListener(e -> new RecipeController().init());

            JButton btnUsuarios = new JButton("3. Administrar Usuarios");
            add(btnUsuarios);
            btnUsuarios.addActionListener(e -> new UserController().init());

            JButton btnPedidos = new JButton("4. Gestión de Pedidos");
            add(btnPedidos);
            btnPedidos.addActionListener(e -> new controller.impl.OrderController().init());

            JButton btnBackup = new JButton("5. Copias de Seguridad");
            add(btnBackup);
            btnBackup.addActionListener(e -> new BackupController().init());
            
            add(new JSeparator());
        } 
        
        JButton btnCocina = new JButton(">>> ENTRAR A COCINA (Órdenes) <<<");
        btnCocina.setBackground(new Color(252, 209, 198));
        add(btnCocina);

        btnCocina.addActionListener(e -> new ChefController().init());

        // 4. Botón Salir (Para todos)
        JButton btnSalir = new JButton("Cerrar Sesión");
        add(btnSalir);
        btnSalir.addActionListener(e -> {
            dispose();
            new controller.impl.LoginController().init();
        });
    }
}