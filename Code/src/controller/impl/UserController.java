package controller.impl;

import model.dao.UserDAO;
import model.entities.User;
import util.SessionController;
import view.impl.UserFormView;
import view.impl.UserListView;
import javax.swing.JOptionPane;
import java.util.List;

public class UserController {
    private UserListView view;
    private UserDAO dao;

    public UserController() {
        this.view = new UserListView();
        this.dao = new UserDAO();
        initController();
    }

    private void initController() {
        // Botón Nuevo
        view.getBtnNuevo().addActionListener(e -> mostrarFormulario());
        
        // Botón Eliminar
        view.getBtnEliminar().addActionListener(e -> eliminarUsuario());
    }

    public void init() {
        // SEGURIDAD: Solo gerente entra aquí
        if (!SessionController.getInstance().esGerente()) {
            JOptionPane.showMessageDialog(null, "Acceso denegado. Solo Gerentes.");
            return;
        }
        cargarDatos();
        view.setVisible(true);
    }

    private void cargarDatos() {
        List<User> usuarios = dao.list();
        view.mostrarUsuarios(usuarios);
    }

    private void mostrarFormulario() {
        UserFormView form = new UserFormView(view);
        form.addGuardarListener(e -> {
            User nuevo = new User(0, form.getUsername(), form.getPassword(), form.getRole());
            if (dao.create(nuevo)) {
                JOptionPane.showMessageDialog(form, "Usuario creado.");
                form.dispose();
                cargarDatos();
            } else {
                JOptionPane.showMessageDialog(form, "Error: Quizás el usuario ya existe.");
            }
        });
        form.setVisible(true);
    }

    private void eliminarUsuario() {
        int id = view.getIdSeleccionado();
        if (id == -1) {
            JOptionPane.showMessageDialog(view, "Selecciona un usuario.");
            return;
        }
        
        // Evitar auto-eliminarse
        if (id == SessionController.getInstance().getUser().getId()) {
            JOptionPane.showMessageDialog(view, "No puedes eliminar tu propio usuario.");
            return;
        }

        if (dao.delete(id)) {
            JOptionPane.showMessageDialog(view, "Usuario eliminado.");
            cargarDatos();
        } else {
            JOptionPane.showMessageDialog(view, "Error al eliminar.");
        }
    }
}