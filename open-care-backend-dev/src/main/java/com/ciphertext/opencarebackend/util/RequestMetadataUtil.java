package com.ciphertext.opencarebackend.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * Utility class to extract metadata from HTTP requests
 * such as IP address, device type, browser, and operating system.
 */
@Slf4j
@Component
public class RequestMetadataUtil {

    private static final Pattern IPV4_PATTERN = Pattern.compile(
            "^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$"
    );

    private static final Pattern IPV6_PATTERN = Pattern.compile(
            "^(?:[0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$|^::(?:[0-9a-fA-F]{1,4}:){0,6}[0-9a-fA-F]{1,4}$|^[0-9a-fA-F]{1,4}::(?:[0-9a-fA-F]{1,4}:){0,5}[0-9a-fA-F]{1,4}$"
    );

    /**
     * Extracts the client IP address from the HTTP request.
     * Checks various headers commonly used by proxies and load balancers.
     *
     * @param request the HTTP servlet request
     * @return the client IP address, or "Unknown" if cannot be determined
     */
    public String extractClientIp(HttpServletRequest request) {
        if (request == null) {
            log.warn("HttpServletRequest is null");
            return "Unknown";
        }

        String[] headerNames = {
                "X-Forwarded-For",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_X_FORWARDED_FOR",
                "HTTP_X_FORWARDED",
                "HTTP_X_CLUSTER_CLIENT_IP",
                "HTTP_CLIENT_IP",
                "HTTP_FORWARDED_FOR",
                "HTTP_FORWARDED",
                "HTTP_VIA",
                "REMOTE_ADDR"
        };

        for (String header : headerNames) {
            String ip = request.getHeader(header);
            if (isValidIp(ip)) {
                // X-Forwarded-For can contain multiple IPs, take the first one
                String clientIp = ip.split(",")[0].trim();
                log.debug("Extracted client IP from header '{}': {}", header, clientIp);
                return clientIp;
            }
        }

        String remoteAddr = request.getRemoteAddr();
        log.debug("Extracted client IP from remote address: {}", remoteAddr);
        return remoteAddr != null ? remoteAddr : "Unknown";
    }

    /**
     * Determines the device type from the User-Agent header.
     *
     * @param request the HTTP servlet request
     * @return "Mobile", "Tablet", "Desktop", or "Unknown Device"
     */
    public String extractDevice(HttpServletRequest request) {
        String userAgent = getUserAgent(request);
        if (userAgent == null) {
            return "Unknown Device";
        }

        userAgent = userAgent.toLowerCase();

        // Check for tablet first (more specific than mobile)
        if (userAgent.contains("ipad") ||
                userAgent.contains("tablet") ||
                userAgent.contains("kindle") ||
                (userAgent.contains("android") && !userAgent.contains("mobile"))) {
            log.debug("Detected device type: Tablet");
            return "Tablet";
        }

        // Check for mobile devices
        if (userAgent.contains("mobile") ||
                userAgent.contains("android") ||
                userAgent.contains("iphone") ||
                userAgent.contains("ipod") ||
                userAgent.contains("blackberry") ||
                userAgent.contains("windows phone") ||
                userAgent.contains("webos")) {
            log.debug("Detected device type: Mobile");
            return "Mobile";
        }

        // Default to desktop
        log.debug("Detected device type: Desktop");
        return "Desktop";
    }

    /**
     * Extracts the browser name from the User-Agent header.
     *
     * @param request the HTTP servlet request
     * @return the browser name, or "Unknown Browser"
     */
    public String extractBrowser(HttpServletRequest request) {
        String userAgent = getUserAgent(request);
        if (userAgent == null) {
            return "Unknown Browser";
        }

        userAgent = userAgent.toLowerCase();

        String browser;
        if (userAgent.contains("edg")) {
            browser = "Edge";
        } else if (userAgent.contains("chrome") && !userAgent.contains("edg")) {
            browser = "Chrome";
        } else if (userAgent.contains("safari") && !userAgent.contains("chrome")) {
            browser = "Safari";
        } else if (userAgent.contains("firefox")) {
            browser = "Firefox";
        } else if (userAgent.contains("opera") || userAgent.contains("opr")) {
            browser = "Opera";
        } else if (userAgent.contains("msie") || userAgent.contains("trident")) {
            browser = "Internet Explorer";
        } else if (userAgent.contains("brave")) {
            browser = "Brave";
        } else if (userAgent.contains("vivaldi")) {
            browser = "Vivaldi";
        } else {
            browser = "Unknown Browser";
        }

        log.debug("Detected browser: {}", browser);
        return browser;
    }

    /**
     * Extracts the operating system from the User-Agent header.
     *
     * @param request the HTTP servlet request
     * @return the operating system name, or "Unknown OS"
     */
    public String extractOperatingSystem(HttpServletRequest request) {
        String userAgent = getUserAgent(request);
        if (userAgent == null) {
            return "Unknown OS";
        }

        userAgent = userAgent.toLowerCase();

        String os;
        if (userAgent.contains("windows nt 10.0")) {
            os = "Windows 10/11";
        } else if (userAgent.contains("windows nt 6.3")) {
            os = "Windows 8.1";
        } else if (userAgent.contains("windows nt 6.2")) {
            os = "Windows 8";
        } else if (userAgent.contains("windows nt 6.1")) {
            os = "Windows 7";
        } else if (userAgent.contains("windows")) {
            os = "Windows";
        } else if (userAgent.contains("mac os x")) {
            os = "macOS";
        } else if (userAgent.contains("mac")) {
            os = "Mac";
        } else if (userAgent.contains("android")) {
            os = "Android";
        } else if (userAgent.contains("iphone") || userAgent.contains("ipad") || userAgent.contains("ipod")) {
            os = "iOS";
        } else if (userAgent.contains("linux")) {
            os = "Linux";
        } else if (userAgent.contains("ubuntu")) {
            os = "Ubuntu";
        } else if (userAgent.contains("fedora")) {
            os = "Fedora";
        } else if (userAgent.contains("chromeos")) {
            os = "Chrome OS";
        } else {
            os = "Unknown OS";
        }

        log.debug("Detected operating system: {}", os);
        return os;
    }

    /**
     * Extracts all metadata in a single call for convenience.
     *
     * @param request the HTTP servlet request
     * @return RequestMetadata object containing all extracted information
     */
    public RequestMetadata extractAllMetadata(HttpServletRequest request) {
        return RequestMetadata.builder()
                .ipAddress(extractClientIp(request))
                .device(extractDevice(request))
                .browser(extractBrowser(request))
                .operatingSystem(extractOperatingSystem(request))
                .userAgent(getUserAgent(request))
                .build();
    }

    /**
     * Validates if the given IP address is a valid IPv4 or IPv6 address.
     *
     * @param ip the IP address to validate
     * @return true if valid, false otherwise
     */
    private boolean isValidIp(String ip) {
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            return false;
        }

        // Remove any whitespace
        ip = ip.trim();

        // Check for IPv4 or IPv6
        return IPV4_PATTERN.matcher(ip).matches() || IPV6_PATTERN.matcher(ip).matches();
    }

    /**
     * Safely extracts the User-Agent header from the request.
     *
     * @param request the HTTP servlet request
     * @return the User-Agent string, or null if not present
     */
    private String getUserAgent(HttpServletRequest request) {
        if (request == null) {
            log.warn("HttpServletRequest is null");
            return null;
        }

        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null || userAgent.isEmpty()) {
            log.debug("User-Agent header is null or empty");
            return null;
        }

        return userAgent;
    }

    /**
     * Data class to hold all request metadata.
     */
    @lombok.Data
    @lombok.Builder
    public static class RequestMetadata {
        private String ipAddress;
        private String device;
        private String browser;
        private String operatingSystem;
        private String userAgent;
    }
}