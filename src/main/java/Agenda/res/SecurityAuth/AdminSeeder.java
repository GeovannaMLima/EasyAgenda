package Agenda.res.SecurityAuth;

import Agenda.res.SecurityAuth.service.UserService;
import Agenda.res.models.TipoDeUser;
import Agenda.res.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class AdminSeeder implements ApplicationRunner {

    private final UserService userService;
    private final UserRepository userRepository;

    @Value("${ADMIN_EMAIL:}")
    private String adminEmail;
    @Value("${ADMIN_PASS:}")
    private String adminPassword;

    public AdminSeeder(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (adminEmail.isBlank() || adminPassword.isBlank()) {
            return;
        }
        if (userRepository.existsByEmailUser(adminEmail)) {
            return;
        }
        userService.criarUsuario("Administrador", adminEmail, adminPassword, TipoDeUser.ADMIN);
    }
}