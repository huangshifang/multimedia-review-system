package com.multimediareview.config;

import com.multimediareview.entity.User;
import com.multimediareview.entity.enums.UserRole;
import com.multimediareview.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setName("系统管理员");
            admin.setRole(UserRole.ADMIN);
            userRepository.save(admin);
        }

        if (!userRepository.existsByUsername("judge1")) {
            User judge1 = new User();
            judge1.setUsername("judge1");
            judge1.setPassword(passwordEncoder.encode("judge123"));
            judge1.setName("评委张三");
            judge1.setRole(UserRole.JUDGE);
            userRepository.save(judge1);
        }

        if (!userRepository.existsByUsername("judge2")) {
            User judge2 = new User();
            judge2.setUsername("judge2");
            judge2.setPassword(passwordEncoder.encode("judge123"));
            judge2.setName("评委李四");
            judge2.setRole(UserRole.JUDGE);
            userRepository.save(judge2);
        }

        if (!userRepository.existsByUsername("player1")) {
            User player1 = new User();
            player1.setUsername("player1");
            player1.setPassword(passwordEncoder.encode("player123"));
            player1.setName("选手王五");
            player1.setRole(UserRole.PARTICIPANT);
            userRepository.save(player1);
        }
    }
}
