package com.labplatform.lab_platform_backend.config;

import com.labplatform.lab_platform_backend.service.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final CustomUserDetailsService userDetailsService;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter,
                          CustomUserDetailsService userDetailsService,
                          OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
        this.oAuth2LoginSuccessHandler = oAuth2LoginSuccessHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            DaoAuthenticationProvider provider) {
        return new ProviderManager(provider);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "http://localhost:5174"
        ));

        config.setAllowedMethods(
                List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        config.setAllowedHeaders(List.of("*"));

        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", config);

        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http

                .csrf(csrf -> csrf.disable())

                .cors(cors ->
                        cors.configurationSource(corsConfigurationSource()))

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth

                        // Public APIs
                        .requestMatchers(
                                "/api/auth/**",
                                "/oauth2/**",
                                "/login/**"
                        ).permitAll()

                        // Equipment
                        .requestMatchers(HttpMethod.POST, "/api/equipment/**")
                        .hasAnyRole(
                                "LAB_MANAGER",
                                "INSTITUTION_ADMIN",
                                "SYSTEM_ADMIN"
                        )

                        .requestMatchers(HttpMethod.PUT, "/api/equipment/**")
                        .hasAnyRole(
                                "LAB_MANAGER",
                                "INSTITUTION_ADMIN",
                                "SYSTEM_ADMIN"
                        )

                        .requestMatchers(HttpMethod.DELETE, "/api/equipment/**")
                        .hasAnyRole(
                                "INSTITUTION_ADMIN",
                                "SYSTEM_ADMIN"
                        )

                        .requestMatchers(HttpMethod.PUT, "/api/equipment/*/share")
                        .hasAnyRole(
                                "LAB_MANAGER",
                                "DEPARTMENT_HEAD",
                                "INSTITUTION_ADMIN",
                                "SYSTEM_ADMIN"
                        )

                        // Booking Approval
                        .requestMatchers(HttpMethod.PUT, "/api/bookings/**")
                        .hasAnyRole(
                                "LAB_MANAGER",
                                "DEPARTMENT_HEAD",
                                "INSTITUTION_ADMIN",
                                "SYSTEM_ADMIN"
                        )

                        // Sharing Requests
                        .requestMatchers(HttpMethod.POST, "/api/sharing-requests")
                        .hasRole("RESEARCHER")

                        .requestMatchers(HttpMethod.PUT, "/api/sharing-requests/**")
                        .hasAnyRole(
                                "LAB_MANAGER",
                                "DEPARTMENT_HEAD",
                                "INSTITUTION_ADMIN",
                                "SYSTEM_ADMIN"
                        )

                        // Analytics
                        .requestMatchers(HttpMethod.GET, "/api/analytics/**")
                        .hasAnyRole(
                                "LAB_MANAGER",
                                "DEPARTMENT_HEAD",
                                "INSTITUTION_ADMIN",
                                "SYSTEM_ADMIN"
                        )

                        // Dashboard
                        .requestMatchers(HttpMethod.GET, "/api/dashboard/**")
                        .authenticated()

                        // Everything else
                        .anyRequest().authenticated()
                )

                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(
                                new HttpStatusEntryPoint(
                                        HttpStatus.UNAUTHORIZED))
                        .accessDeniedHandler((request,
                                              response,
                                              accessDeniedException) ->
                                response.sendError(
                                        HttpServletResponse.SC_FORBIDDEN,
                                        "Forbidden"))
                )

                .oauth2Login(oauth2 ->
                        oauth2.successHandler(oAuth2LoginSuccessHandler)
                )

                .addFilterBefore(
                        jwtAuthFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}