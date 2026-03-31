package com.ciphertext.opencarebackend;

import com.ciphertext.opencarebackend.config.PortConflictFailureListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
@EnableCaching
public class OpenCareBackendApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(OpenCareBackendApplication.class);
        application.addListeners(new PortConflictFailureListener());
        application.run(args);
    }

}
