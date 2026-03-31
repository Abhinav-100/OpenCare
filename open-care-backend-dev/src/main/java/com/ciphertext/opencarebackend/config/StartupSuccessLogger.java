package com.ciphertext.opencarebackend.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class StartupSuccessLogger {

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady(ApplicationReadyEvent event) {
        Environment environment = event.getApplicationContext().getEnvironment();

        String port = environment.getProperty("local.server.port", environment.getProperty("server.port", "6700"));
        String contextPath = normalizeContextPath(environment.getProperty("server.servlet.context-path", ""));
        String baseUrl = "http://localhost:" + port + contextPath;

        log.info("OpenCare backend started successfully on port {}.", port);
        log.info("Swagger UI available at: {}/swagger-ui.html", baseUrl);
        log.info("Readiness endpoint: {}/actuator/health/readiness", baseUrl);
    }

    private String normalizeContextPath(String contextPath) {
        if (contextPath == null || contextPath.isBlank() || "/".equals(contextPath.trim())) {
            return "";
        }

        String trimmed = contextPath.trim();
        return trimmed.startsWith("/") ? trimmed : "/" + trimmed;
    }
}