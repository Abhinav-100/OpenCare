package com.ciphertext.opencarebackend.modules.content.service.impl;

import com.ciphertext.opencarebackend.entity.Doctor;
import com.ciphertext.opencarebackend.entity.Hospital;
import com.ciphertext.opencarebackend.entity.Institution;
import com.ciphertext.opencarebackend.mapper.DoctorMapper;
import com.ciphertext.opencarebackend.mapper.HospitalMapper;
import com.ciphertext.opencarebackend.mapper.InstitutionMapper;
import com.ciphertext.opencarebackend.modules.content.service.SearchService;
import com.ciphertext.opencarebackend.modules.provider.dto.response.DoctorResponse;
import com.ciphertext.opencarebackend.modules.provider.dto.response.HospitalResponse;
import com.ciphertext.opencarebackend.modules.provider.dto.response.InstitutionResponse;
import com.ciphertext.opencarebackend.modules.provider.repository.DoctorRepository;
import com.ciphertext.opencarebackend.modules.provider.repository.HospitalRepository;
import com.ciphertext.opencarebackend.modules.provider.repository.InstitutionRepository;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;





@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final DoctorRepository doctorRepository;
    private final HospitalRepository hospitalRepository;
    private final InstitutionRepository institutionRepository;
    private final DoctorMapper doctorMapper;
    private final HospitalMapper hospitalMapper;
    private final InstitutionMapper institutionMapper;

    @Override
    public Map<String, Object> generalSearch(String query, int limitPerDomain, 
                                           boolean includeDoctors, boolean includeHospitals, 
                                           boolean includeInstitutions) {
        
        Map<String, Object> results = new HashMap<>();
        Pageable pageable = PageRequest.of(0, limitPerDomain);

        if (includeDoctors) {
            List<DoctorResponse> doctors = searchDoctors(query, pageable);
            results.put("doctors", doctors);
            results.put("doctorCount", doctors.size());
        }

        if (includeHospitals) {
            List<HospitalResponse> hospitals = searchHospitals(query, pageable);
            results.put("hospitals", hospitals);
            results.put("hospitalCount", hospitals.size());
        }

        if (includeInstitutions) {
            List<InstitutionResponse> institutions = searchInstitutions(query, pageable);
            results.put("institutions", institutions);
            results.put("institutionCount", institutions.size());
        }

        // Add search metadata
        results.put("query", query);
        results.put("totalResults", results.values().stream()
                .filter(v -> v instanceof List)
                .mapToInt(v -> ((List<?>) v).size())
                .sum());
        results.put("timestamp", new Date());

        return results;
    }

    @Override
    public List<String> getSearchSuggestions(String partial, int limit) {
        Set<String> suggestions = new LinkedHashSet<>();
        
        // Get suggestions from doctors
        suggestions.addAll(getDoctorSuggestions(partial, limit / 3));
        
        // Get suggestions from hospitals
        suggestions.addAll(getHospitalSuggestions(partial, limit / 3));
        
        // Get suggestions from institutions
        suggestions.addAll(getInstitutionSuggestions(partial, limit / 3));
        
        return suggestions.stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Long> getSearchStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("doctors", doctorRepository.count());
        stats.put("hospitals", hospitalRepository.count());
        stats.put("institutions", institutionRepository.count());
        stats.put("total", stats.values().stream().mapToLong(Long::longValue).sum());
        return stats;
    }

    // Private helper methods for domain-specific searches
    private List<DoctorResponse> searchDoctors(String query, Pageable pageable) {
        Specification<Doctor> spec = createDoctorSearchSpecification(query);
        return doctorRepository.findAll(spec, pageable)
                .getContent()
                .stream()
                .map(doctorMapper::toResponse)
                .collect(Collectors.toList());
    }

    private List<HospitalResponse> searchHospitals(String query, Pageable pageable) {
        Specification<Hospital> spec = createHospitalSearchSpecification(query);
        return hospitalRepository.findAll(spec, pageable)
                .getContent()
                .stream()
                .map(hospitalMapper::toResponse)
                .collect(Collectors.toList());
    }

    private List<InstitutionResponse> searchInstitutions(String query, Pageable pageable) {
        Specification<Institution> spec = createInstitutionSearchSpecification(query);
        return institutionRepository.findAll(spec, pageable)
                .getContent()
                .stream()
                .map(institutionMapper::toResponse)
                .collect(Collectors.toList());
    }

    // Search specifications for each domain
    private Specification<Doctor> createDoctorSearchSpecification(String query) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (query == null || query.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            String searchTerm = "%" + query.toLowerCase() + "%";
            
            return criteriaBuilder.or(
                // Search in doctor profile name
                criteriaBuilder.like(
                    criteriaBuilder.lower(root.join("profile").get("name")), searchTerm),
                // Search in doctor profile Bengali name
                criteriaBuilder.like(
                    criteriaBuilder.lower(root.join("profile").get("bnName")), searchTerm),
                // Search in BMDC number
                criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("bmdcNo")), searchTerm),
                // Search in specializations
                criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("specializations")), searchTerm),
                // Search in degrees
                criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("degrees")), searchTerm),
                // Search in description
                criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("description")), searchTerm)
            );
        };
    }

    private Specification<Hospital> createHospitalSearchSpecification(String query) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (query == null || query.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            String searchTerm = "%" + query.toLowerCase() + "%";
            
            return criteriaBuilder.or(
                // Search in hospital name
                criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("name")), searchTerm),
                // Search in hospital Bengali name
                criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("bnName")), searchTerm),
                // Search in website URL
                criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("websiteUrl")), searchTerm)
            );
        };
    }

    private Specification<Institution> createInstitutionSearchSpecification(String query) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (query == null || query.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            String searchTerm = "%" + query.toLowerCase() + "%";
            
            return criteriaBuilder.or(
                // Search in institution name
                criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("name")), searchTerm),
                // Search in institution Bengali name
                criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("bnName")), searchTerm),
                // Search in website URL
                criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("websiteUrl")), searchTerm)
            );
        };
    }

    // Suggestion methods
    private List<String> getDoctorSuggestions(String partial, int limit) {
        if (partial == null || partial.trim().isEmpty()) {
            return Collections.emptyList();
        }
        
        String searchTerm = "%" + partial.toLowerCase() + "%";
        Pageable pageable = PageRequest.of(0, limit);
        
        Specification<Doctor> spec = (root, criteriaQuery, criteriaBuilder) ->
            criteriaBuilder.or(
                criteriaBuilder.like(
                    criteriaBuilder.lower(root.join("profile").get("name")), searchTerm),
                criteriaBuilder.like(
                    criteriaBuilder.lower(root.join("profile").get("bnName")), searchTerm)
            );
        
        return doctorRepository.findAll(spec, pageable)
                .getContent()
                .stream()
                .map(doctor -> doctor.getProfile().getName())
                .distinct()
                .collect(Collectors.toList());
    }

    private List<String> getHospitalSuggestions(String partial, int limit) {
        if (partial == null || partial.trim().isEmpty()) {
            return Collections.emptyList();
        }
        
        String searchTerm = "%" + partial.toLowerCase() + "%";
        Pageable pageable = PageRequest.of(0, limit);
        
        Specification<Hospital> spec = (root, criteriaQuery, criteriaBuilder) ->
            criteriaBuilder.or(
                criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("name")), searchTerm),
                criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("bnName")), searchTerm)
            );
        
        return hospitalRepository.findAll(spec, pageable)
                .getContent()
                .stream()
                .map(Hospital::getName)
                .distinct()
                .collect(Collectors.toList());
    }

    private List<String> getInstitutionSuggestions(String partial, int limit) {
        if (partial == null || partial.trim().isEmpty()) {
            return Collections.emptyList();
        }
        
        String searchTerm = "%" + partial.toLowerCase() + "%";
        Pageable pageable = PageRequest.of(0, limit);
        
        Specification<Institution> spec = (root, criteriaQuery, criteriaBuilder) ->
            criteriaBuilder.or(
                criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("name")), searchTerm),
                criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("bnName")), searchTerm)
            );
        
        return institutionRepository.findAll(spec, pageable)
                .getContent()
                .stream()
                .map(Institution::getName)
                .distinct()
                .collect(Collectors.toList());
    }
}