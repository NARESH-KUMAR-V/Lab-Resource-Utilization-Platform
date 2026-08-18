package com.labplatform.lab_platform_backend;

import com.labplatform.lab_platform_backend.entity.User;
import com.labplatform.lab_platform_backend.entity.UserStatus;
import com.labplatform.lab_platform_backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@SpringBootTest
public class UpdatePasswordTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void setAllUserPasswords() {
        String newPassword = "Password@123";
        List<User> users = userRepository.findAll();
        for (User user : users) {
            if (!"GOOGLE".equalsIgnoreCase(user.getAuthProvider())) {
                user.setPassword(passwordEncoder.encode(newPassword));
                if ("systemadmin@labsys.edu".equalsIgnoreCase(user.getEmail())) {
                    user.setStatus(UserStatus.ACTIVE);
                }
                userRepository.save(user);
                System.out.println("Updated password to Password@123 for user: " + user.getEmail() + " (" + user.getRole() + ")");
            }
        }
        System.out.println(">>> ALL_NON_GOOGLE_USERS_UPDATED_TO_Password@123 <<<");
    }
}
