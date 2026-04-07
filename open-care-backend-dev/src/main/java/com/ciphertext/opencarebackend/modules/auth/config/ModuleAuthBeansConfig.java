package com.ciphertext.opencarebackend.modules.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
/**
 * Flow note: ModuleAuthBeansConfig belongs to the authentication module.
 * Read this with neighboring controller/service/repository files to trace request flow.
 */
public class ModuleAuthBeansConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}