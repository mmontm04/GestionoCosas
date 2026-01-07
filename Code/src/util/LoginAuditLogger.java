package util;

import model.entities.User;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LoginAuditLogger {
    private static final Path LOG_PATH = Paths.get("logs", "login_audit.log");
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void logLogin(User user) {
        if (user == null) {
            return;
        }

        String timestamp = LocalDateTime.now().format(FORMATTER);
        String line = String.format("%s | usuario=%s | rol=%s%n",
            timestamp,
            user.getUsername(),
            user.getRole()
        );

        try {
            Files.createDirectories(LOG_PATH.getParent());
            Files.write(LOG_PATH, line.getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE,
                StandardOpenOption.APPEND
            );
        } catch (IOException e) {
            System.err.println("Error al registrar log de inicio de sesión: " + e.getMessage());
        }
    }
}
