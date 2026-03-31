package com.ciphertext.opencarebackend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "app.search")
@Data
public class SearchConfig {
    
    /**
     * Whether search functionality is enabled
     */
    private boolean enabled = true;
    
    /**
     * Maximum results per domain in general search
     */
    private int maxResultsPerDomain = 10;
    
    /**
     * Maximum suggestions for autocomplete
     */
    private int maxSuggestions = 20;
    
    /**
     * Search domains configuration
     */
    private Map<String, SearchDomainConfig> domains;
    
    /**
     * Default search weights for different fields
     */
    private Map<String, Double> fieldWeights;
    
    /**
     * Search result caching configuration
     */
    private CacheConfig cache = new CacheConfig();
    
    @Data
    public static class SearchDomainConfig {
        private boolean enabled = true;
        private int maxResults = 100;
        private List<String> searchableFields;
        private double weight = 1.0;
    }
    
    @Data
    public static class CacheConfig {
        private boolean enabled = true;
        private int ttlSeconds = 300; // 5 minutes
        private int maxSize = 1000;
    }
}