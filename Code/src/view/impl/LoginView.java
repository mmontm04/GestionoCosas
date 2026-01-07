package view.impl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class LoginView extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;

    public LoginView() {
        setTitle("Acceso al Sistema - GestionoCosas");
        setSize(350, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centrar en pantalla
        setLayout(new GridLayout(3, 2, 10, 10));

        // Componentes
        add(new JLabel("Usuario:", SwingConstants.CENTER));
        txtUsername = new JTextField();
        add(txtUsername);

        add(new JLabel("Contraseña:", SwingConstants.CENTER));
        txtPassword = new JPasswordField();
        add(txtPassword);

        // Botón vacío (se rellena el espacio izquierdo)
        add(new JLabel("")); 
        btnLogin = new JButton("Entrar");
        add(btnLogin);
    }

    public String getUsername() {
        return txtUsername.getText();
    }

    public String getPassword() {
        return new String(txtPassword.getPassword());
    }

    // Método para que el Controlador escuche el clic
    public void addLoginListener(ActionListener listener) {
        btnLogin.addActionListener(listener);
    }

    public void showMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg);
    }
}