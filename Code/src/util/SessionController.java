package util;

import model.entities.User;

import util.LoginAuditLogger;

public class SessionController {
    private static SessionController instance;
    private User usuarioLogueado;
    private final LoginAuditLogger loginAuditLogger = new LoginAuditLogger();

    private SessionController() {}

    public static SessionController getInstance() {
        if (instance == null) {
            instance = new SessionController();
        }
        return instance;
    }

    public void login(User user) {
        this.usuarioLogueado = user;
        loginAuditLogger.logLogin(user);
    }

    public void logout() {
        this.usuarioLogueado = null;
    }

    public User getUser() {
        return usuarioLogueado;
    }

    // Método helper para verificar rol rápidamente
    public boolean esGerente() {
        return usuarioLogueado != null && "GERENTE".equalsIgnoreCase(usuarioLogueado.getRole());
    }
}
