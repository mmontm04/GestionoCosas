package controller.impl;

import model.dao.IUserDAO;
import model.dao.UserDAO;
import model.entities.User;
import view.impl.LoginView;
import view.impl.MainDashboardView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginController {
    private LoginView view;
    private IUserDAO userDAO;

    public LoginController() {
        this.view = new LoginView();
        this.userDAO = new UserDAO();
        
        // Escuchar el botón de la vista
        this.view.addLoginListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                verificarCredenciales();
            }
        });
    }

    public void init() {
        view.setVisible(true);
    }

    private void verificarCredenciales() {
        String user = view.getUsername();
        String pass = view.getPassword();

        // Validar campos vacíos
        if (user.isEmpty() || pass.isEmpty()) {
            view.showMessage("Por favor, rellena todos los campos.");
            return;
        }

        // Consultar a la BD (Modelo)
        User usuarioEncontrado = userDAO.findByUsername(user);

        if (usuarioEncontrado != null && usuarioEncontrado.getPassword().equals(pass)) {
            util.SessionController.getInstance().login(usuarioEncontrado);

            view.showMessage("¡Bienvenido " + usuarioEncontrado.getRole() + "!");
            
            if (usuarioEncontrado != null && usuarioEncontrado.getPassword().equals(pass)) {
            view.dispose(); 
            
            MainDashboardView dashboard = new MainDashboardView(usuarioEncontrado.getRole());
            dashboard.setVisible(true);
            
            } else {
                view.showMessage("Usuario o contraseña incorrectos.");
            }
            
            // TODO: Iniciar Dashboard según rol
            System.out.println("Login correcto. Rol: " + usuarioEncontrado.getRole());
            
        } else {
            view.showMessage("Usuario o contraseña incorrectos.");
        }
    }
}