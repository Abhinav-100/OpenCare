package com.ciphertext.opencarebackend.modules.provider.service.impl;
import com.ciphertext.opencarebackend.modules.provider.dto.filter.NurseFilter;
import com.ciphertext.opencarebackend.modules.auth.dto.request.KeycloakRegistrationRequest;
import com.ciphertext.opencarebackend.entity.Nurse;
import com.ciphertext.opencarebackend.enums.UserType;
import com.ciphertext.opencarebackend.exception.BadRequestException;
import com.ciphertext.opencarebackend.exception.ResourceNotFoundException;
import com.ciphertext.opencarebackend.modules.provider.repository.NurseRepository;
import com.ciphertext.opencarebackend.modules.shared.repository.specification.Filter;
import com.ciphertext.opencarebackend.modules.shared.repository.specification.MultiJoin;
import com.ciphertext.opencarebackend.modules.provider.service.NurseService;
import com.ciphertext.opencarebackend.modules.auth.service.KeycloakService;
import com.ciphertext.opencarebackend.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.ciphertext.opencarebackend.modules.shared.repository.specification.QueryFilterUtils.*;
import static com.ciphertext.opencarebackend.modules.shared.repository.specification.QueryOperator.*;
import static com.ciphertext.opencarebackend.modules.shared.repository.specification.SpecificationBuilder.createSpecification;
import static org.springframework.data.jpa.domain.Specification.where;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class NurseServiceImpl implements NurseService {

    private final NurseRepository nurseRepository;
    private final KeycloakService keycloakService;



    @Override
    public List<Nurse> getAllNurses() {
        log.info("Fetching all nurses");
        List<Nurse> nurses = nurseRepository.findAll();
        log.info("Retrieved {} nurses", nurses.size());
        return nurseRepository.findAll();
    }

    @Override
    public Page<Nurse> getPaginatedDataWithFilters(NurseFilter nurseFilter, Pageable pagingSort) {
        log.info("Fetching nurses with filters: {}", nurseFilter);
        List<Filter> filterList = generateQueryFilters(nurseFilter);
        Specification<Nurse> specification = where(null);

        if (!filterList.isEmpty()) {
            specification = where(createSpecification(filterList.removeFirst()));
            for (Filter input : filterList) {
                specification = specification.and(createSpecification(input));
            }
        }
        log.info("Fetching nurses with filters: {}", nurseFilter);
        return nurseRepository.findAll(specification, pagingSort);
    }

    @Override
    public Nurse getNurseById(Long id) throws ResourceNotFoundException {
        if (id <= 0) {
            log.error("Invalid nurse ID: {}", id);
            throw new BadRequestException("nurse ID must be positive");
        }

        log.info("Fetching nurse with id: {}", id);
        return nurseRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Nurse not found with id: {}", id);
                    return new ResourceNotFoundException("Nurse not found with id: " + id);
                });
    }

    @Override
    public Nurse createNurse(Nurse nurse) {
        log.info("Creating nurse: {}", nurse);
        return nurseRepository.save(nurse);
    }

    @Override
    public Nurse updateNurse(Nurse newNurse, Long nurseId) {
        log.info("Updating nurse: {}", newNurse);
        return nurseRepository.findById(nurseId)

                .map(nurse -> {
                    nurse.setStartDate(newNurse.getStartDate());
                    nurse.setBnmcNo(newNurse.getBnmcNo());
                    nurse.setDescription(newNurse.getDescription());
                    return nurseRepository.save(nurse);
                })
                .orElseGet(() -> {
                    newNurse.setId(nurseId);
                    return nurseRepository.save(newNurse);
                });
    }

    @Override
    public ResponseEntity<Object> deleteNurseById(Long nurseId) {
        log.info("Deleting nurse with id: {}", nurseId);
        nurseRepository.deleteById(nurseId);
        if (nurseRepository.findById(nurseId).isPresent()) {
            return ResponseEntity.unprocessableEntity().body("Failed to delete the specified record");
        } else return ResponseEntity.ok().body("Nurse deleted successfully");
    }



    private List<Filter> generateQueryFilters(NurseFilter nurseFilter) {
        List<Filter> filters = new ArrayList<>();

        if (nurseFilter.getName() != null)
            filters.add(generateJoinTableFilter("name", "profile", LIKE_JOIN, nurseFilter.getName()));

        if (nurseFilter.getBnName() != null)
            filters.add(generateJoinTableFilter("bnName", "profile", LIKE_JOIN, nurseFilter.getBnName()));

        if (nurseFilter.getDistrictId() != null) {
            Set<MultiJoin> multiJoins = new LinkedHashSet<>();
            multiJoins.add(new MultiJoin("id", "profile", null));
            multiJoins.add(new MultiJoin("id", "district", nurseFilter.getDistrictId()));
            filters.add(generateMultiJoinTableFilter(multiJoins, MULTI_JOIN));
        }

        if (nurseFilter.getUpazilaId() != null){
            Set<MultiJoin> multiJoins = new LinkedHashSet<>();
            multiJoins.add(new MultiJoin("id", "profile", null));
            multiJoins.add(new MultiJoin("id", "upazila", nurseFilter.getUpazilaId()));
            filters.add(generateMultiJoinTableFilter(multiJoins, MULTI_JOIN));
        }

        if (nurseFilter.getUnionId() != null){
            Set<MultiJoin> multiJoins = new LinkedHashSet<>();
            multiJoins.add(new MultiJoin("id", "profile", null));
            multiJoins.add(new MultiJoin("id", "union", nurseFilter.getUnionId()));
            filters.add(generateMultiJoinTableFilter(multiJoins, MULTI_JOIN));
        }

        return filters;
    }


    @Override
    public String createNurseUser(Long nurseId) {
        // create keycloak user for nurse
        log.info("Creating Keycloak user for nurse with ID: {}", nurseId);
        Nurse nurse = getNurseById(nurseId);
        if (nurse == null) {
            log.error("Nurse not found with ID: {}", nurseId);
            throw new ResourceNotFoundException("Nurse not found with ID: " + nurseId);
        }
        if (nurse.getProfile() == null || nurse.getProfile().getEmail() == null) {
            log.error("Nurse profile or email is null for nurse ID: {}", nurseId);
            throw new BadRequestException("Nurse profile or email is not set");
        }

        String email = nurse.getProfile().getEmail();
        log.info("Creating Keycloak user with email: {}", email);

        KeycloakRegistrationRequest.Credential credential = new KeycloakRegistrationRequest.Credential();
        credential.setType("password");
        credential.setValue(StringUtil.generateRandomPassword(8));
        credential.setTemporary(false);

        KeycloakRegistrationRequest keycloakRegistrationRequest = KeycloakRegistrationRequest.builder()
                .username(nurse.getProfile().getPhone())
                .email(email)
                .firstName(nurse.getProfile().getName())
                .lastName(nurse.getProfile().getBnName())
                .credentials(List.of(credential))
                .enabled(true)
                .build();

        keycloakService.registerUser(keycloakRegistrationRequest, UserType.NURSE.getKeycloakGroupName());
        log.info("Keycloak user created successfully for nurse with ID: {}", nurseId);

        return "Nurse user created successfully with ID: " + nurseId;
    }

}
