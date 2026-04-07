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
        // Create the Spring application explicitly so custom listeners can be attached before boot.
        SpringApplication application = new SpringApplication(OpenCareBackendApplication.class);
        // Gives a friendly error when configured port is already occupied.
        application.addListeners(new PortConflictFailureListener());
        // Bootstraps Spring context: configs -> beans -> web server -> ready state.
        application.run(args);
    }

}
