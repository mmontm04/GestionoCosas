package controller.impl;

import model.dao.UserDAO;
import model.entities.User;
import util.SessionController;
import view.UserFormView;
import view.UserListView;

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
        view.getBtnNuevo().addActionListener(e -> mostrarFormulario());
        view.getBtnEditar().addActionListener(e -> editarUsuario());
        view.getBtnEliminar().addActionListener(e -> eliminarUsuario());
    }

    public void init() {
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
                registrarAuditoria("ALTA_USUARIO", "Creado usuario: " + nuevo.getUsername());
                JOptionPane.showMessageDialog(form, "Usuario creado.");
                form.dispose();
                cargarDatos();
            } else {
                JOptionPane.showMessageDialog(form, "Error: Quizás el usuario ya existe.");
            }
        });
        form.setVisible(true);
    }

    private void editarUsuario() {
        int id = view.getIdSeleccionado();
        if (id == -1) {
            JOptionPane.showMessageDialog(view, "Selecciona un usuario para editar.");
            return;
        }


        User usuarioAEditar = dao.findById(id);

        if (usuarioAEditar == null) {
            JOptionPane.showMessageDialog(view, "Error: No se encuentra el usuario.");
            return;
        }

        UserFormView form = new UserFormView(view);
        
        form.setUsername(usuarioAEditar.getUsername());
        form.setPassword(usuarioAEditar.getPassword()); 
        form.setRole(usuarioAEditar.getRole());

        final User u = usuarioAEditar; 
        
        form.addGuardarListener(e -> {
            User usuarioEditado = new User(
                u.getId(), 
                form.getUsername(), 
                form.getPassword(), 
                form.getRole()
            );

            if (dao.update(usuarioEditado)) {
                registrarAuditoria("MODIFICACION_USUARIO", "Editado ID: " + u.getId());
                JOptionPane.showMessageDialog(form, "Usuario actualizado correctamente.");
                form.dispose();
                cargarDatos();
            } else {
                JOptionPane.showMessageDialog(form, "Error al actualizar. Comprueba el código.");
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
        
        if (id == SessionController.getInstance().getUser().getId()) {
            JOptionPane.showMessageDialog(view, "No puedes eliminar tu propio usuario.");
            return;
        }

        if (dao.delete(id)) {
            registrarAuditoria("BAJA_USUARIO", "Eliminado ID: " + id);
            JOptionPane.showMessageDialog(view, "Usuario eliminado.");
            cargarDatos();
        } else {
            JOptionPane.showMessageDialog(view, "Error al eliminar.");
        }
    }

    private void registrarAuditoria(String accion, String detalle) {        
        String sql = "INSERT INTO logs (usuario, accion, detalle) VALUES (?, ?, ?)";
        try (java.sql.Connection conn = config.DbConnection.getInstance().getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            String usuarioLogueado = SessionController.getInstance().getUser().getUsername();
            
            pstmt.setString(1, usuarioLogueado);
            pstmt.setString(2, accion);
            pstmt.setString(3, detalle);
            pstmt.executeUpdate();
            
        } catch (Exception e) {
            System.err.println("No se pudo guardar el log: " + e.getMessage());
        }
    }
}