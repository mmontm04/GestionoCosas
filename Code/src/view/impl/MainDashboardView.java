package view.impl;

import controller.impl.ProductController;
import javax.swing.*;
import java.awt.*;

public class MainDashboardView extends JFrame {

    public MainDashboardView(String rol) {
        setTitle("Sistema de Gestión - Rol: " + rol);
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Al cerrar esto, se acaba el programa
        setLocationRelativeTo(null);
        setLayout(new GridLayout(3, 1, 10, 10));

        JLabel lblBienvenida = new JLabel("Bienvenido al Sistema", SwingConstants.CENTER);
        lblBienvenida.setFont(new Font("Arial", Font.BOLD, 20));
        add(lblBienvenida);

        JButton btnProductos = new JButton("Gestionar Productos");
        add(btnProductos);

        // Acción del botón: Abrir el controlador de productos
        btnProductos.addActionListener(e -> {
            ProductController pc = new ProductController();
            pc.init();
        });

        JButton btnRecetas = new JButton("Gestión de Recetas");
        add(btnRecetas);

        btnRecetas.addActionListener(e -> {
            controller.impl.RecipeController rc = new controller.impl.RecipeController();
            rc.init();
        });

        JButton btnUsuarios = new JButton("Administrar Usuarios");
        add(btnUsuarios);

        btnUsuarios.addActionListener(e -> {
            controller.impl.UserController uc = new controller.impl.UserController();
            uc.init();
        });
        
        // Aquí irían más botones (Recetas, Usuarios...)
    }
}