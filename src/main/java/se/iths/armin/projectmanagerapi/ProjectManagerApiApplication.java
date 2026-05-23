package se.iths.armin.projectmanagerapi;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import se.iths.armin.projectmanagerapi.entity.AppUser;
import se.iths.armin.projectmanagerapi.entity.enums.UserPosition;
import se.iths.armin.projectmanagerapi.entity.enums.UserStatus;
import se.iths.armin.projectmanagerapi.repository.AppUserRepository;

@SpringBootApplication
public class ProjectManagerApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProjectManagerApiApplication.class, args);
    }

    @Bean
    CommandLineRunner bootstrapAdmin(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            String adminEmail = "zandarmin1@gmail.com";

            if (appUserRepository.findByEmail(adminEmail).isPresent()) {
                return;
            }

            AppUser admin = new AppUser();
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode("password"));
            admin.setFirstname("Inanna");
            admin.setLastname("Klinghult");
            admin.setUserPosition(UserPosition.EMPLOYEE);
            admin.setUserStatus(UserStatus.ACTIVE);

            appUserRepository.save(admin);
        };
    }


}
