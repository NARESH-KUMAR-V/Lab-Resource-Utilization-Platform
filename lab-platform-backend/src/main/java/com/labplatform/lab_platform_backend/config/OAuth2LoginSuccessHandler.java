package com.labplatform.lab_platform_backend.config;

import com.labplatform.lab_platform_backend.entity.User;
import com.labplatform.lab_platform_backend.entity.UserStatus;
import com.labplatform.lab_platform_backend.repository.UserRepository;
import com.labplatform.lab_platform_backend.service.JwtService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    public OAuth2LoginSuccessHandler(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        Optional<User> existingUserOpt = userRepository.findByEmail(email);

        if (existingUserOpt.isEmpty()) {
            // New Google account -> Redirect to Registration page to pick Role, Institution, Lab & Dept
            String redirectUrl = "https://lab-resource-utilization-platform-8viy.onrender.com/register"
                    + "?email=" + URLEncoder.encode(email, StandardCharsets.UTF_8)
                    + "&name=" + URLEncoder.encode(name != null ? name : "", StandardCharsets.UTF_8)
                    + "&fromGoogle=true";
            response.sendRedirect(redirectUrl);
            return;
        }

        User user = existingUserOpt.get();

        if (user.getStatus() == UserStatus.REJECTED) {
            // Allow rejected users to re-submit their registration details
            String redirectUrl = "https://lab-resource-utilization-platform-8viy.onrender.com/register"
                    + "?email=" + URLEncoder.encode(email, StandardCharsets.UTF_8)
                    + "&name=" + URLEncoder.encode(name != null ? name : user.getName(), StandardCharsets.UTF_8)
                    + "&fromGoogle=true"
                    + "&reapply=true";
            response.sendRedirect(redirectUrl);
            return;
        }

        if (user.getStatus() == UserStatus.PENDING) {
            // If incomplete Google registration (missing institution selection), redirect to register page to complete
            if (user.getInstitution() == null) {
                String redirectUrl = "https://lab-resource-utilization-platform-8viy.onrender.com/register"
                        + "?email=" + URLEncoder.encode(email, StandardCharsets.UTF_8)
                        + "&name=" + URLEncoder.encode(name != null ? name : user.getName(), StandardCharsets.UTF_8)
                        + "&fromGoogle=true";
                response.sendRedirect(redirectUrl);
                return;
            }

            response.sendRedirect("https://lab-resource-utilization-platform-8viy.onrender.com/login?error=" + URLEncoder.encode("Your registration is awaiting System Admin approval.", StandardCharsets.UTF_8));
            return;
        }

        String token = jwtService.generateToken(user);

        Long institutionId = user.getInstitution() != null
                ? user.getInstitution().getId()
                : null;

        Long laboratoryId = user.getLaboratory() != null
                ? user.getLaboratory().getId()
                : null;

        response.sendRedirect(
                "https://lab-resource-utilization-platform-8viy.onrender.com/oauth-success"
                        + "?token=" + token
                        + "&id=" + user.getId()
                        + "&name=" + URLEncoder.encode(user.getName(), StandardCharsets.UTF_8)
                        + "&email=" + URLEncoder.encode(user.getEmail(), StandardCharsets.UTF_8)
                        + "&role=" + user.getRole().name()
                        + "&institutionId=" + (institutionId != null ? institutionId : "")
                        + "&laboratoryId=" + (laboratoryId != null ? laboratoryId : "")
        );
    }
}