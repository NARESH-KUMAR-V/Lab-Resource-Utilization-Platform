package com.labplatform.lab_platform_backend.config;

import com.labplatform.lab_platform_backend.entity.User;
import com.labplatform.lab_platform_backend.entity.UserStatus;
import com.labplatform.lab_platform_backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
public class DevDataInitializer {

    @Bean
    public CommandLineRunner initDevPasswords(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            String defaultPassword = "Password@123";
            List<User> allUsers = userRepository.findAll();
            int updatedCount = 0;

            for (User user : allUsers) {
                // Skip Google OAuth users
                if ("GOOGLE".equalsIgnoreCase(user.getAuthProvider())) {
                    continue;
                }

                if (!passwordEncoder.matches(defaultPassword, user.getPassword())) {
                    user.setPassword(passwordEncoder.encode(defaultPassword));
                    if (user.getStatus() == UserStatus.PENDING && "systemadmin@labsys.edu".equalsIgnoreCase(user.getEmail())) {
                        user.setStatus(UserStatus.ACTIVE);
                    }
                    userRepository.save(user);
                    updatedCount++;
                }
            }

            if (updatedCount > 0) {
                System.out.println("✅ [DevDataInitializer] Updated " + updatedCount + " user passwords to 'Password@123'");
            }
        };
    }
}
