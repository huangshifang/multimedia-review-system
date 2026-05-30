package com.multimediareview.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/competitions/*/files/**").permitAll()
                .requestMatchers("/api/participant/**").hasRole("PARTICIPANT")
                .requestMatchers("/api/competitions/*/scores/**").hasRole("JUDGE")
                .requestMatchers("/api/competitions/*/judges/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/competitions/*/participants/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/competitions/*/participants/**").hasRole("ADMIN")
                .requestMatchers("/api/users/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/competitions/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/competitions/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/competitions/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/competitions/**").authenticated()
                .anyRequest().authenticated()
            )
            .headers(headers -> headers.frameOptions(fo -> fo.disable()))
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
