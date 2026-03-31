package com.ciphertext.opencarebackend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.boot.web.server.PortInUseException;
import org.springframework.context.ApplicationListener;

public class PortConflictFailureListener implements ApplicationListener<ApplicationFailedEvent> {

    private static final Logger log = LoggerFactory.getLogger(PortConflictFailureListener.class);

    @Override
    public void onApplicationEvent(ApplicationFailedEvent event) {
        PortInUseException portInUseException = findCause(event.getException(), PortInUseException.class);
        if (portInUseException == null) {
            return;
        }

        int port = resolveConfiguredPort(event.getArgs(), portInUseException.getPort());
        log.error("Port {} is already in use. OpenCare backend failed to start.", port);
        log.error("Startup stopped before serving requests.");
        log.error("Fix options:");
        log.error("1) Set SERVER_PORT, e.g. SERVER_PORT=6701");
        log.error("2) Pass --server.port=6701 at startup");
        log.error("3) Stop the process currently listening on port {}", port);
    }

    private int resolveConfiguredPort(String[] args, int fallbackPort) {
        Integer fromArgs = extractPortFromArgs(args);
        if (fromArgs != null) {
            return fromArgs;
        }

        Integer fromSystemProperty = parseInteger(System.getProperty("server.port"));
        if (fromSystemProperty != null) {
            return fromSystemProperty;
        }

        Integer fromLegacyEnv = parseInteger(System.getenv("SERVER_PORT"));
        if (fromLegacyEnv != null) {
            return fromLegacyEnv;
        }

        if (fallbackPort > 0) {
            return fallbackPort;
        }
        return 6700;
    }

    private Integer extractPortFromArgs(String[] args) {
        if (args == null) {
            return null;
        }

        for (String arg : args) {
            if (arg == null) {
                continue;
            }

            if (arg.startsWith("--server.port=")) {
                return parseInteger(arg.substring("--server.port=".length()));
            }

            if (arg.startsWith("-Dserver.port=")) {
                return parseInteger(arg.substring("-Dserver.port=".length()));
            }
        }

        return null;
    }

    private Integer parseInteger(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private <T extends Throwable> T findCause(Throwable throwable, Class<T> causeClass) {
        Throwable current = throwable;
        while (current != null) {
            if (causeClass.isInstance(current)) {
                return causeClass.cast(current);
            }
            current = current.getCause();
        }
        return null;
    }
}