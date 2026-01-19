package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class UserFormView extends JDialog {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<String> comboRol;
    private JButton btnGuardar;

    public UserFormView(Frame parent) {
        super(parent, "Registrar Usuario", true);
        setSize(300, 250);
        setLocationRelativeTo(parent);
        setLayout(new GridLayout(4, 2, 10, 10));

        add(new JLabel("  Usuario:"));
        txtUsername = new JTextField();
        add(txtUsername);

        add(new JLabel("  Contraseña:"));
        txtPassword = new JPasswordField();
        add(txtPassword);

        add(new JLabel("  Rol:"));
        comboRol = new JComboBox<>(new String[]{"COCINERO", "GERENTE"});
        add(comboRol);

        add(new JLabel(""));
        btnGuardar = new JButton("Guardar");
        add(btnGuardar);
    }

    public void setUsername(String username) { this.txtUsername.setText(username); }
    public void setPassword(String password) { this.txtPassword.setText(password); }
    public void setRole(String role) { this.comboRol.setSelectedItem(role); }

    public String getUsername() { return txtUsername.getText(); }
    public String getPassword() { return new String(txtPassword.getPassword()); }
    public String getRole() { return (String) comboRol.getSelectedItem(); }

    public void addGuardarListener(ActionListener l) {
        btnGuardar.addActionListener(l);
    }
}