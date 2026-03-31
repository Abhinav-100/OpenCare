package com.ciphertext.opencarebackend.modules.content.service;

import java.util.List;
import java.util.Map;

public interface SearchService {
    
    /**
     * General search across all domains with configurable inclusion
     * @param query Search term
     * @param limitPerDomain Maximum results per domain
     * @param includeDoctors Whether to include doctors in search
     * @param includeHospitals Whether to include hospitals in search
     * @param includeInstitutions Whether to include institutions in search
     * @return Map containing search results grouped by domain
     */
    Map<String, Object> generalSearch(String query, int limitPerDomain, 
                                     boolean includeDoctors, boolean includeHospitals, 
                                     boolean includeInstitutions);
    
    /**
     * Get search suggestions for autocomplete
     * @param partial Partial search term
     * @param limit Maximum number of suggestions
     * @return List of search suggestions
     */
    List<String> getSearchSuggestions(String partial, int limit);
    
    /**
     * Get search statistics across all domains
     * @return Map containing count of searchable items per domain
     */
    Map<String, Long> getSearchStats();
}