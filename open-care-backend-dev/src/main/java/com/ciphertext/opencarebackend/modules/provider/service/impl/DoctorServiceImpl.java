package com.ciphertext.opencarebackend.modules.provider.service.impl;

import com.ciphertext.opencarebackend.entity.Doctor;
import com.ciphertext.opencarebackend.entity.DoctorAssociation;
import com.ciphertext.opencarebackend.entity.DoctorDegree;
import com.ciphertext.opencarebackend.entity.DoctorWorkplace;
import com.ciphertext.opencarebackend.entity.Hospital;
import com.ciphertext.opencarebackend.entity.Profile;
import com.ciphertext.opencarebackend.entity.Tag;
import com.ciphertext.opencarebackend.enums.UserType;
import com.ciphertext.opencarebackend.exception.BadRequestException;
import com.ciphertext.opencarebackend.exception.DuplicateResourceException;
import com.ciphertext.opencarebackend.exception.ResourceNotFoundException;
import com.ciphertext.opencarebackend.exception.UnprocessableEntityException;
import com.ciphertext.opencarebackend.mapper.DoctorMapper;
import com.ciphertext.opencarebackend.modules.auth.dto.request.KeycloakRegistrationRequest;
import com.ciphertext.opencarebackend.modules.auth.service.KeycloakService;
import com.ciphertext.opencarebackend.modules.catalog.service.LocationService;
import com.ciphertext.opencarebackend.modules.catalog.service.TagService;
import com.ciphertext.opencarebackend.modules.provider.dto.filter.DoctorFilter;
import com.ciphertext.opencarebackend.modules.provider.dto.request.DoctorRequest;
import com.ciphertext.opencarebackend.modules.provider.repository.DoctorAssociationRepository;
import com.ciphertext.opencarebackend.modules.provider.repository.DoctorDegreeRepository;
import com.ciphertext.opencarebackend.modules.provider.repository.DoctorRepository;
import com.ciphertext.opencarebackend.modules.provider.repository.DoctorScheduleRepository;
import com.ciphertext.opencarebackend.modules.provider.repository.DoctorWorkplaceRepository;
import com.ciphertext.opencarebackend.modules.provider.repository.HospitalRepository;
import com.ciphertext.opencarebackend.modules.provider.service.DegreeService;
import com.ciphertext.opencarebackend.modules.provider.service.DoctorService;
import com.ciphertext.opencarebackend.modules.provider.service.MedicalSpecialityService;
import com.ciphertext.opencarebackend.modules.shared.repository.specification.Filter;
import com.ciphertext.opencarebackend.modules.shared.repository.specification.MultiJoin;
import com.ciphertext.opencarebackend.modules.shared.repository.specification.MultiJoinIn;
import com.ciphertext.opencarebackend.modules.user.service.ProfileService;
import com.ciphertext.opencarebackend.util.StringUtil;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.ciphertext.opencarebackend.modules.shared.repository.specification.QueryFilterUtils.*;
import static com.ciphertext.opencarebackend.modules.shared.repository.specification.QueryOperator.*;
import static com.ciphertext.opencarebackend.modules.shared.repository.specification.SpecificationBuilder.*;
import static org.springframework.data.jpa.domain.Specification.where;




@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {
    private final DoctorRepository doctorRepository;
    private final DoctorDegreeRepository doctorDegreeRepository;
    private final DoctorWorkplaceRepository doctorWorkplaceRepository;
    private final DoctorAssociationRepository doctorAssociationRepository;
    private final DoctorScheduleRepository doctorScheduleRepository;
    private final KeycloakService keycloakService;
    private final TagService tagService;
    private final MedicalSpecialityService medicalSpecialityService;
    private final LocationService locationService;
    private final DegreeService degreeService;
    private final ProfileService profileService;
    private final DoctorMapper doctorMapper;
    private final HospitalRepository hospitalRepository;

    private static final String PUBLIC_DOCTOR_NOT_FOUND = "Doctor not found";

    @Override
    public Long getDoctorCount() {
        return doctorRepository.count();
    }

    @Override
    public List<Doctor> getAllDoctors() {
        log.info("Fetching all doctors");
        List<Doctor> doctors = doctorRepository.findByIsActiveTrueAndIsVerifiedTrue();
        log.info("Retrieved {} doctors", doctors.size());
        return doctors;
    }

    @Override
    public Page<Doctor> getPaginatedDataWithFilters(DoctorFilter doctorFilter, Pageable pagingSort) {
        log.info("Fetching doctors with filters: {}", doctorFilter);
        if (doctorFilter == null) {
            doctorFilter = DoctorFilter.builder().build();
        }

        List<Filter> filterList = generateQueryFilters(doctorFilter);
        Specification<Doctor> specification = where(isPublicDoctorVisible());
        if(!filterList.isEmpty()) {
            specification = specification.and(createSpecification(filterList.removeFirst()));
            for (Filter input : filterList) {
                specification = specification.and(createSpecification(input));
            }
        }
        if (doctorFilter.getDegreeId() != null) {
            specification = specification.and(hasDegree(doctorFilter.getDegreeId()));
        }
        if (doctorFilter.getDegreeIds() != null && !doctorFilter.getDegreeIds().isEmpty()) {
            specification = specification.and(hasDegreeIn(doctorFilter.getDegreeIds()));
        }
        if (doctorFilter.getAssociationId() != null) {
            specification = specification.and(hasAssociation(doctorFilter.getAssociationId()));
        }
        if (doctorFilter.getHospitalId() != null) {
            specification = specification.and(worksAtHospital(doctorFilter.getHospitalId(), doctorFilter.getIsCurrentWorkplace()));
        }
        if (doctorFilter.getHospitalIds() != null && !doctorFilter.getHospitalIds().isEmpty()) {
            specification = specification.and(worksAtHospitalIn(doctorFilter.getHospitalIds(), doctorFilter.getIsCurrentWorkplace()));
        }
        if( doctorFilter.getWorkInstitutionId() != null) {
            specification = specification.and(worksAtInstitution(doctorFilter.getWorkInstitutionId(), doctorFilter.getIsCurrentWorkplace()));
        }
        if (doctorFilter.getInstitutionIds() != null && !doctorFilter.getInstitutionIds().isEmpty()) {
            Specification<Doctor> institutionSpec = worksAtInstitutionIn(doctorFilter.getInstitutionIds(), doctorFilter.getIsCurrentWorkplace())
                    .or(studyAtInstitutionIn(doctorFilter.getInstitutionIds()));
            specification = specification.and(institutionSpec);
        }
        if (doctorFilter.getStudyInstitutionId() != null) {
            specification = specification.and(studyAtInstitution(doctorFilter.getStudyInstitutionId()));
        }
        if (doctorFilter.getSpecialityId() != null) {
            Specification<Doctor> specialitySpec =
                    hasDegreeSpeciality(doctorFilter.getSpecialityId())
                            .or(hasHospitalSpeciality(doctorFilter.getSpecialityId()));

            specification = specification.and(specialitySpec);
        }
        if (doctorFilter.getSpecialityIds() != null && !doctorFilter.getSpecialityIds().isEmpty()) {
            Specification<Doctor> specialitySpec =
                    hasDegreeSpecialityIn(doctorFilter.getSpecialityIds())
                            .or(hasHospitalSpecialityIn(doctorFilter.getSpecialityIds()));
            specification = specification.and(specialitySpec);
        }
        if (doctorFilter.getMinExperience() != null) {
            LocalDate cutoff = LocalDate.now().minusYears(doctorFilter.getMinExperience());
            specification = specification.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("startDate"), cutoff));
        }
        if (doctorFilter.getMaxExperience() != null) {
            LocalDate cutoff = LocalDate.now().minusYears(doctorFilter.getMaxExperience());
            specification = specification.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("startDate"), cutoff));
        }
        log.info("Fetching doctors with filters: {}", doctorFilter);
        return doctorRepository.findAll(specification, pagingSort);
    }

    @Override
    public Doctor getDoctorById(Long id) throws ResourceNotFoundException {
        if (id == null || id <= 0) {
            log.error("Invalid doctor ID: {}", id);
            throw new BadRequestException("Doctor ID must be positive");
        }

        log.info("Fetching doctor with id: {}", id);
        return doctorRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Doctor not found with id: {}", id);
                    return new ResourceNotFoundException("Doctor not found with id: " + id);
                });
    }

    @Override
    public Doctor getPublicDoctorById(Long id) throws ResourceNotFoundException {
        if (id == null || id <= 0) {
            log.error("Invalid doctor ID: {}", id);
            throw new BadRequestException("Doctor ID must be positive");
        }

        log.info("Fetching publicly visible doctor with id: {}", id);
        return doctorRepository.findByIdAndIsActiveTrueAndIsVerifiedTrue(id)
                .orElseThrow(() -> {
                    log.warn("Public doctor not found or not visible with id: {}", id);
                    return new ResourceNotFoundException(PUBLIC_DOCTOR_NOT_FOUND);
                });
    }


    @Override
    public Doctor getDoctorByEmail(String email) throws ResourceNotFoundException {
        if (email == null || email.trim().isEmpty()) {
            throw new BadRequestException("Doctor email is required");
        }
        log.info("Fetching doctor with email: {}", email);
        return doctorRepository.findByProfileEmail(email)
                .orElseThrow(() -> {
                    log.error("Doctor not found with email: {}", email);
                    return new ResourceNotFoundException("Doctor not found with email: " + email);
                });
    }

    @Override
    public Doctor getPublicDoctorByEmail(String email) throws ResourceNotFoundException {
        if (email == null || email.trim().isEmpty()) {
            throw new BadRequestException("Doctor email is required");
        }
        log.info("Fetching publicly visible doctor with email: {}", email);
        return doctorRepository.findByProfileEmailAndIsActiveTrueAndIsVerifiedTrue(email)
                .orElseThrow(() -> {
                    log.warn("Public doctor not found or not visible with email: {}", email);
                    return new ResourceNotFoundException(PUBLIC_DOCTOR_NOT_FOUND);
                });
    }

    @Override
    public Doctor getDoctorByUsername(String username) throws ResourceNotFoundException {
        if (username == null || username.trim().isEmpty()) {
            throw new BadRequestException("Doctor username is required");
        }
        log.info("Fetching doctor with username: {}", username);
        return doctorRepository.findByProfileUsername(username)
                .orElseThrow(() -> {
                    log.error("Doctor not found with username: {}", username);
                    return new ResourceNotFoundException("Doctor not found with username: " + username);
                });
    }

    @Override
    public Doctor getPublicDoctorByUsername(String username) throws ResourceNotFoundException {
        if (username == null || username.trim().isEmpty()) {
            throw new BadRequestException("Doctor username is required");
        }
        log.info("Fetching publicly visible doctor with username: {}", username);
        return doctorRepository.findByProfileUsernameAndIsActiveTrueAndIsVerifiedTrue(username)
                .orElseThrow(() -> {
                    log.warn("Public doctor not found or not visible with username: {}", username);
                    return new ResourceNotFoundException(PUBLIC_DOCTOR_NOT_FOUND);
                });
    }

    @Override
    public Doctor getDoctorByBmdcNo(String bmdcNo) throws ResourceNotFoundException {
        if (bmdcNo == null || bmdcNo.trim().isEmpty()) {
            throw new BadRequestException("BMDC number is required");
        }
        log.info("Fetching doctor with BMDC number: {}", bmdcNo);
        return doctorRepository.findByBmdcNo(bmdcNo)
                .orElseThrow(() -> {
                    log.error("Doctor not found with BMDC number: {}", bmdcNo);
                    return new ResourceNotFoundException("Doctor not found with BMDC number: " + bmdcNo);
                });
    }

    @Override
    public Doctor getPublicDoctorByBmdcNo(String bmdcNo) throws ResourceNotFoundException {
        if (bmdcNo == null || bmdcNo.trim().isEmpty()) {
            throw new BadRequestException("BMDC number is required");
        }
        log.info("Fetching publicly visible doctor with BMDC number: {}", bmdcNo);
        return doctorRepository.findByBmdcNoAndIsActiveTrueAndIsVerifiedTrue(bmdcNo)
                .orElseThrow(() -> {
                    log.warn("Public doctor not found or not visible with BMDC number: {}", bmdcNo);
                    return new ResourceNotFoundException(PUBLIC_DOCTOR_NOT_FOUND);
                });
    }

    @Override
    public Doctor createDoctor(DoctorRequest request) {
        log.info("Creating doctor: {}", request.getName());

        // Check for duplicate email
        if (request.getEmail() != null && doctorRepository.existsByProfileEmail(request.getEmail())) {
            throw new DuplicateResourceException("Doctor with email " + request.getEmail() + " already exists");
        }

        // Check for duplicate username
        if (request.getUsername() != null && doctorRepository.existsByProfileUsername(request.getUsername())) {
            throw new DuplicateResourceException("Doctor with username " + request.getUsername() + " already exists");
        }

        // Check for duplicate BMDC number
        if (request.getBmdcNo() != null && doctorRepository.existsByBmdcNo(request.getBmdcNo())) {
            throw new DuplicateResourceException("Doctor with BMDC number " + request.getBmdcNo() + " already exists");
        }

        Doctor doctor = doctorMapper.toEntity(request);

        if (doctor.getHospital() != null && doctor.getHospital().getId() != null) {
            Hospital hospital = hospitalRepository.findById(doctor.getHospital().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Hospital not found with id: " + doctor.getHospital().getId()));
            doctor.setHospital(hospital);
        } else {
            doctor.setHospital(null);
        }

        // Handle tags
        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            Set<Tag> tags = fetchTags(request.getTagIds());
            doctor.setTags(tags);
            log.info("Setting {} tags for doctor", tags.size());
        }

        // Create profile with location references
        if (doctor.getProfile() != null) {
            handleProfileLocation(doctor.getProfile(), request.getDistrictId(), request.getUpazilaId(), request.getUnionId());
            doctor.setProfile(profileService.createProfile(doctor.getProfile()));
            log.info("Profile created with ID: {}", doctor.getProfile().getId());
        }

        Doctor savedDoctor = doctorRepository.save(doctor);
        log.info("Created doctor with id: {}", savedDoctor.getId());
        return savedDoctor;
    }

    @Override
    public Doctor updateDoctor(Long id, DoctorRequest request) {
        log.info("Updating doctor with id: {}", id);

        Doctor doctor = getDoctorById(id);

        // Check email uniqueness if changed
        if (request.getEmail() != null &&
            (doctor.getProfile() == null || !request.getEmail().equals(doctor.getProfile().getEmail()))) {
            if (doctorRepository.existsByProfileEmail(request.getEmail())) {
                throw new DuplicateResourceException("Doctor with email " + request.getEmail() + " already exists");
            }
        }

        // Check username uniqueness if changed
        if (request.getUsername() != null &&
            (doctor.getProfile() == null || !request.getUsername().equals(doctor.getProfile().getUsername()))) {
            if (doctorRepository.existsByProfileUsername(request.getUsername())) {
                throw new DuplicateResourceException("Doctor with username " + request.getUsername() + " already exists");
            }
        }

        // Check BMDC number uniqueness if changed
        if (request.getBmdcNo() != null && !request.getBmdcNo().equals(doctor.getBmdcNo())) {
            if (doctorRepository.existsByBmdcNo(request.getBmdcNo())) {
                throw new DuplicateResourceException("Doctor with BMDC number " + request.getBmdcNo() + " already exists");
            }
        }

        // Handle tags update
        if (request.getTagIds() != null) {
            Set<Tag> tags = fetchTags(request.getTagIds());
            doctor.setTags(tags);
            log.info("Updating {} tags for doctor", tags.size());
        }

        // Partial update
        doctorMapper.partialUpdate(request, doctor);

        if (request.getHospitalId() != null) {
            Hospital hospital = hospitalRepository.findById(request.getHospitalId())
                .orElseThrow(() -> new ResourceNotFoundException("Hospital not found with id: " + request.getHospitalId()));
            doctor.setHospital(hospital);
        }

        // Handle profile location update
        if (doctor.getProfile() != null) {
            handleProfileLocation(doctor.getProfile(), request.getDistrictId(), request.getUpazilaId(), request.getUnionId());
            if (doctor.getProfile().getId() != null) {
                doctor.setProfile(profileService.updateProfile(doctor.getProfile().getId(), doctor.getProfile()));
            } else {
                doctor.setProfile(profileService.createProfile(doctor.getProfile()));
            }
        }

        Doctor updatedDoctor = doctorRepository.save(doctor);
        log.info("Updated doctor with id: {}", updatedDoctor.getId());
        return updatedDoctor;
    }

    @Override
    public void verifyDoctor(Long id) {
        log.info("Verifying doctor with id: {}", id);
        Doctor doctor = getDoctorById(id);
        doctor.setIsVerified(true);
        doctorRepository.save(doctor);
        log.info("Doctor verified with id: {}", id);
    }

    @Override
    public void activateDoctor(Long id) {
        log.info("Activating doctor with id: {}", id);
        Doctor doctor = getDoctorById(id);
        doctor.setIsActive(true);
        doctorRepository.save(doctor);
        log.info("Doctor activated with id: {}", id);
    }

    @Override
    public void deactivateDoctor(Long id) {
        log.info("Deactivating doctor with id: {}", id);
        Doctor doctor = getDoctorById(id);
        doctor.setIsActive(false);
        doctorRepository.save(doctor);
        log.info("Doctor deactivated with id: {}", id);
    }

    @Override
    public void approveDoctor(Long id) {
        log.info("Approving doctor with id: {}", id);
        Doctor doctor = getDoctorById(id);
        doctor.setIsVerified(true);
        doctor.setIsActive(true);
        doctorRepository.save(doctor);
        log.info("Doctor approved with id: {}", id);
    }

    private void handleProfileLocation(Profile profile, Integer districtId, Integer upazilaId, Integer unionId) {
        if (districtId != null) {
            profile.setDistrict(locationService.getDistrictById(districtId));
        }

        if (upazilaId != null) {
            profile.setUpazila(locationService.getUpazilaById(upazilaId));
        }

        if (unionId != null) {
            log.debug("Union reference provided: {}", unionId);
        }
    }

    private Set<Tag> fetchTags(Set<Integer> tagIds) {
        Set<Tag> tags = new HashSet<>();
        for (Integer tagId : tagIds) {
            tagService.getTagById(tagId).ifPresent(tags::add);
        }
        return tags;
    }

    @Override
    public void deleteDoctorById(Long doctorId) {
        log.info("Deleting doctor with id: {}", doctorId);
        Doctor doctor = getDoctorById(doctorId);
        checkDoctorDependencies(doctorId);
        doctorRepository.delete(doctor);
        if (doctorRepository.findById(doctorId).isPresent()) {
            throw new UnprocessableEntityException("Failed to delete doctor");
        }
    }

    @Override
    public List<Map<String, Object>> quickSearch(String query, int limit) {
        log.info("Quick search for doctors with query: '{}' and limit: {}", query, limit);
        
        Pageable pageable = PageRequest.of(0, limit);
        Specification<Doctor> spec = where(isPublicDoctorVisible()).and(createQuickSearchSpecification(query));
        
        return doctorRepository.findAll(spec, pageable)
                .getContent()
                .stream()
                .map(this::createQuickSearchResult)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, List<String>> getSearchSuggestions() {
        log.info("Getting doctor search suggestions");
        
        Map<String, List<String>> suggestions = new HashMap<>();
        
        // Get speciality suggestions
        suggestions.put("specialities", getSpecialitySuggestions());
        
        // Get location suggestions
        suggestions.put("districts", getDistrictSuggestions());
        suggestions.put("upazilas", getUpazilaSuggestions());
        suggestions.put("unions", getUnionSuggestions());
        
        // Get degree suggestions
        suggestions.put("degrees", getDegreeSuggestions());
        
        return suggestions;
    }

    private Map<String, Object> createQuickSearchResult(Doctor doctor) {
        Map<String, Object> result = new HashMap<>();
        result.put("id", doctor.getId());
        result.put("name", doctor.getProfile() != null ? doctor.getProfile().getName() : "");
        result.put("bnName", doctor.getProfile() != null ? doctor.getProfile().getBnName() : "");
        result.put("bmdcNo", doctor.getBmdcNo());
        result.put("specializations", doctor.getSpecializations());
        result.put("degrees", doctor.getDegrees());
        result.put("isVerified", doctor.getIsVerified());
        result.put("isActive", doctor.getIsActive());
        result.put("type", "doctor");
        
        return result;
    }

    private Specification<Doctor> createQuickSearchSpecification(String query) {
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
                    criteriaBuilder.lower(root.get("degrees")), searchTerm)
            );
        };
    }

    private List<String> getSpecialitySuggestions() {
        // This would typically come from a medical speciality service
        return medicalSpecialityService.getAllSpecialities()
                .stream()
                .map(speciality -> speciality.getName())
                .collect(Collectors.toList());
    }

    private List<String> getDistrictSuggestions() {
        return locationService.getAllDistricts().stream()
                .map(district -> district.getName())
                .collect(Collectors.toList());
    }

    private List<String> getUpazilaSuggestions() {
        return locationService.getAllUpazilas().stream()
                .map(district -> district.getName())
                .collect(Collectors.toList());
    }

    private List<String> getUnionSuggestions() {
        // This would typically come from a location service
        return List.of("Union 1", "Union 2", "Union 3");
    }

    private List<String> getDegreeSuggestions() {
        return degreeService.getAllDegrees().stream()
                .map(district -> district.getName())
                .collect(Collectors.toList());
    }

    private Specification<Doctor> isPublicDoctorVisible() {
        return (root, query, cb) -> cb.and(
                cb.isTrue(root.get("isActive")),
                cb.isTrue(root.get("isVerified"))
        );
    }

    @Override
    public String createDoctorUser(Long doctorId) {
//        create keycloak user for doctor
        log.info("Creating Keycloak user for doctor with ID: {}", doctorId);
        Doctor doctor = getDoctorById(doctorId);
        if (doctor == null) {
            log.error("Doctor not found with ID: {}", doctorId);
            throw new ResourceNotFoundException("Doctor not found with ID: " + doctorId);
        }
        if (doctor.getProfile() == null || doctor.getProfile().getEmail() == null) {
            log.error("Doctor profile or email is null for doctor ID: {}", doctorId);
            throw new BadRequestException("Doctor profile or email is not set");
        }
        String email = doctor.getProfile().getEmail();
        log.info("Creating Keycloak user with email: {}", email);
        KeycloakRegistrationRequest.Credential credential = new KeycloakRegistrationRequest.Credential();
        credential.setType("password");
        credential.setValue(StringUtil.generateRandomPassword(8));
        credential.setTemporary(false);
        KeycloakRegistrationRequest keycloakRegistrationRequest = KeycloakRegistrationRequest.builder()
                .username(doctor.getProfile().getPhone())
                .email(email)
                .firstName(doctor.getProfile().getName())
                .lastName(doctor.getProfile().getBnName())
                .credentials(List.of(credential))
                .enabled(true)
                .build();
        keycloakService.registerUser(keycloakRegistrationRequest, UserType.DOCTOR.getKeycloakGroupName());
        log.info("Keycloak user created successfully for doctor with ID: {}", doctorId);
        return "Doctor user created successfully with ID: " + doctorId;
    }

    public static Specification<Doctor> hasDegree(Integer degreeId) {
        return (root, query, cb) -> {
            // Prevent duplicate results
            query.distinct(true);

            // Join Doctor -> DoctorDegree
            Root<DoctorDegree> degreeRoot = query.from(DoctorDegree.class);
            Predicate doctorJoin = cb.equal(degreeRoot.get("doctor").get("id"), root.get("id"));
            Predicate degreeMatch = cb.equal(degreeRoot.get("degree").get("id"), degreeId);

            return cb.and(doctorJoin, degreeMatch);
        };
    }

    public static Specification<Doctor> hasDegreeIn(List<Integer> degreeIds) {
        return (root, query, cb) -> {
            query.distinct(true);
            Root<DoctorDegree> degreeRoot = query.from(DoctorDegree.class);
            Predicate doctorJoin = cb.equal(degreeRoot.get("doctor").get("id"), root.get("id"));
            Predicate degreeMatch = degreeRoot.get("degree").get("id").in(degreeIds);
            return cb.and(doctorJoin, degreeMatch);
        };
    }

    public static Specification<Doctor> hasAssociation(Integer associationId) {
        return (root, query, cb) -> {
            // Prevent duplicate results
            query.distinct(true);

            // Join Doctor -> DoctorAssociation
            Root<DoctorAssociation> associationRoot = query.from(DoctorAssociation.class);
            Predicate doctorJoin = cb.equal(associationRoot.get("doctor").get("id"), root.get("id"));
            Predicate associationMatch = cb.equal(associationRoot.get("association").get("id"), associationId);

            return cb.and(doctorJoin, associationMatch);
        };
    }

    public static Specification<Doctor> hasDegreeSpeciality(Integer specialityId) {
        return (root, query, cb) -> {
            // Prevent duplicate results
            query.distinct(true);

            // Join Doctor -> DoctorDegree
            Root<DoctorDegree> degreeRoot = query.from(DoctorDegree.class);
            Predicate doctorJoin = cb.equal(degreeRoot.get("doctor").get("id"), root.get("id"));
            Predicate specialityMatch = cb.equal(degreeRoot.get("medicalSpeciality").get("id"), specialityId);

            return cb.and(doctorJoin, specialityMatch);
        };
    }

    public static Specification<Doctor> hasDegreeSpecialityIn(List<Integer> specialityIds) {
        return (root, query, cb) -> {
            query.distinct(true);
            Root<DoctorDegree> degreeRoot = query.from(DoctorDegree.class);
            Predicate doctorJoin = cb.equal(degreeRoot.get("doctor").get("id"), root.get("id"));
            Predicate specialityMatch = degreeRoot.get("medicalSpeciality").get("id").in(specialityIds);
            return cb.and(doctorJoin, specialityMatch);
        };
    }

    public static Specification<Doctor> hasHospitalSpeciality(Integer specialityId) {
        return (root, query, cb) -> {
            // Prevent duplicate results
            query.distinct(true);

            // Join Doctor -> DoctorWorkplace
            Root<DoctorWorkplace> workplaceRoot = query.from(DoctorWorkplace.class);
            Predicate doctorJoin = cb.equal(workplaceRoot.get("doctor").get("id"), root.get("id"));
            Predicate specialityMatch = cb.equal(workplaceRoot.get("medicalSpeciality").get("id"), specialityId);

            return cb.and(doctorJoin, specialityMatch);
        };
    }

    public static Specification<Doctor> hasHospitalSpecialityIn(List<Integer> specialityIds) {
        return (root, query, cb) -> {
            query.distinct(true);
            Root<DoctorWorkplace> workplaceRoot = query.from(DoctorWorkplace.class);
            Predicate doctorJoin = cb.equal(workplaceRoot.get("doctor").get("id"), root.get("id"));
            Predicate specialityMatch = workplaceRoot.get("medicalSpeciality").get("id").in(specialityIds);
            return cb.and(doctorJoin, specialityMatch);
        };
    }

    public static Specification<Doctor> worksAtHospital(Integer hospitalId, Boolean isCurrentWorkplace) {
        return (root, query, cb) -> {
            // Prevent duplicate results
            query.distinct(true);

            // Join Doctor -> DoctorWorkplace
            Root<DoctorWorkplace> workplaceRoot = query.from(DoctorWorkplace.class);
            Predicate doctorJoin = cb.equal(workplaceRoot.get("doctor").get("id"), root.get("id"));
            Predicate hospitalMatch = cb.equal(workplaceRoot.get("hospital").get("id"), hospitalId);
            if(isCurrentWorkplace != null) {
                Predicate currentWorkplaceMatch = isCurrentWorkplace ?
                        cb.isNull(workplaceRoot.get("endDate")) :
                        cb.isNotNull(workplaceRoot.get("endDate"));
                return cb.and(doctorJoin, hospitalMatch, currentWorkplaceMatch);
            }
            return cb.and(doctorJoin, hospitalMatch);
        };
    }

    public static Specification<Doctor> worksAtHospitalIn(List<Integer> hospitalIds, Boolean isCurrentWorkplace) {
        return (root, query, cb) -> {
            query.distinct(true);
            Root<DoctorWorkplace> workplaceRoot = query.from(DoctorWorkplace.class);
            Predicate doctorJoin = cb.equal(workplaceRoot.get("doctor").get("id"), root.get("id"));
            Predicate hospitalMatch = workplaceRoot.get("hospital").get("id").in(hospitalIds);
            if (isCurrentWorkplace != null) {
                Predicate currentWorkplaceMatch = isCurrentWorkplace ?
                        cb.isNull(workplaceRoot.get("endDate")) :
                        cb.isNotNull(workplaceRoot.get("endDate"));
                return cb.and(doctorJoin, hospitalMatch, currentWorkplaceMatch);
            }
            return cb.and(doctorJoin, hospitalMatch);
        };
    }

    public static Specification<Doctor> worksAtInstitution(Integer institutionId, Boolean isCurrentWorkplace) {
        return (root, query, cb) -> {
            // Prevent duplicate results
            query.distinct(true);

            // Join Doctor -> DoctorWorkplace
            Root<DoctorWorkplace> workplaceRoot = query.from(DoctorWorkplace.class);
            Predicate doctorJoin = cb.equal(workplaceRoot.get("doctor").get("id"), root.get("id"));
            Predicate institutionMatch = cb.equal(workplaceRoot.get("institution").get("id"), institutionId);
            if(isCurrentWorkplace != null) {
                Predicate currentWorkplaceMatch = isCurrentWorkplace ?
                        cb.isNull(workplaceRoot.get("endDate")) :
                        cb.isNotNull(workplaceRoot.get("endDate"));
                return cb.and(doctorJoin, institutionMatch, currentWorkplaceMatch);
            }

            return cb.and(doctorJoin, institutionMatch);
        };
    }

    public static Specification<Doctor> worksAtInstitutionIn(List<Integer> institutionIds, Boolean isCurrentWorkplace) {
        return (root, query, cb) -> {
            query.distinct(true);
            Root<DoctorWorkplace> workplaceRoot = query.from(DoctorWorkplace.class);
            Predicate doctorJoin = cb.equal(workplaceRoot.get("doctor").get("id"), root.get("id"));
            Predicate institutionMatch = workplaceRoot.get("institution").get("id").in(institutionIds);
            if (isCurrentWorkplace != null) {
                Predicate currentWorkplaceMatch = isCurrentWorkplace ?
                        cb.isNull(workplaceRoot.get("endDate")) :
                        cb.isNotNull(workplaceRoot.get("endDate"));
                return cb.and(doctorJoin, institutionMatch, currentWorkplaceMatch);
            }
            return cb.and(doctorJoin, institutionMatch);
        };
    }

    public static Specification<Doctor> studyAtInstitution(Integer institutionId) {
        return (root, query, cb) -> {
            // Prevent duplicate results
            query.distinct(true);

            // Join Doctor -> DoctorDegree
            Root<DoctorDegree> degreeRoot = query.from(DoctorDegree.class);
            Predicate doctorJoin = cb.equal(degreeRoot.get("doctor").get("id"), root.get("id"));
            Predicate institutionMatch = cb.equal(degreeRoot.get("institution").get("id"), institutionId);

            return cb.and(doctorJoin, institutionMatch);
        };
    }

    public static Specification<Doctor> studyAtInstitutionIn(List<Integer> institutionIds) {
        return (root, query, cb) -> {
            query.distinct(true);
            Root<DoctorDegree> degreeRoot = query.from(DoctorDegree.class);
            Predicate doctorJoin = cb.equal(degreeRoot.get("doctor").get("id"), root.get("id"));
            Predicate institutionMatch = degreeRoot.get("institution").get("id").in(institutionIds);
            return cb.and(doctorJoin, institutionMatch);
        };
    }

    private List<Filter> generateQueryFilters(DoctorFilter doctorFilter) {

        List<Filter> filters = new ArrayList<>();

        if (doctorFilter.getName() != null)
            filters.add(generateJoinTableFilter("name", "profile", LIKE_JOIN, doctorFilter.getName()));

        if (doctorFilter.getBnName() != null)
            filters.add(generateJoinTableFilter("bnName", "profile", LIKE_JOIN, doctorFilter.getBnName()));

        if (doctorFilter.getBmdcNo() != null)
            filters.add(generateIndividualFilter("bmdcNo", LIKE, doctorFilter.getBmdcNo()));

        if (doctorFilter.getSpeciality() != null) {
            filters.add(generateIndividualFilter("specializations", LIKE, doctorFilter.getSpeciality()));
        }

        if (doctorFilter.getIsVerified() != null) {
            filters.add(generateIndividualFilter("isVerified", EQUALS, doctorFilter.getIsVerified()));
        }

        if (doctorFilter.getIsActive() != null) {
            filters.add(generateIndividualFilter("isActive", EQUALS, doctorFilter.getIsActive()));
        }

        if (doctorFilter.getDistrictId() != null) {
            Set<MultiJoin> multiJoins = new LinkedHashSet<>();
            multiJoins.add(new MultiJoin("id", "profile", null));
            multiJoins.add(new MultiJoin("id", "district", doctorFilter.getDistrictId()));
            filters.add(generateMultiJoinTableFilter(multiJoins, MULTI_JOIN));
        }

        if (doctorFilter.getDistrictIds() != null && !doctorFilter.getDistrictIds().isEmpty()) {
            Set<MultiJoinIn<?>> joins = new LinkedHashSet<>();
            joins.add(new MultiJoinIn<>("id", "profile", "id", null));
            joins.add(new MultiJoinIn<>("id", "district", "id", doctorFilter.getDistrictIds()));
            filters.add(generateMultiJoinTableInFilter(joins, MULTI_JOIN_IN));
        }

        if (doctorFilter.getUpazilaId() != null){
            Set<MultiJoin> multiJoins = new LinkedHashSet<>();
            multiJoins.add(new MultiJoin("id", "profile", null));
            multiJoins.add(new MultiJoin("id", "upazila", doctorFilter.getUpazilaId()));
            filters.add(generateMultiJoinTableFilter(multiJoins, MULTI_JOIN));
        }

        if (doctorFilter.getUpazilaIds() != null && !doctorFilter.getUpazilaIds().isEmpty()) {
            Set<MultiJoinIn<?>> joins = new LinkedHashSet<>();
            joins.add(new MultiJoinIn<>("id", "profile", "id", null));
            joins.add(new MultiJoinIn<>("id", "upazila", "id", doctorFilter.getUpazilaIds()));
            filters.add(generateMultiJoinTableInFilter(joins, MULTI_JOIN_IN));
        }

        if (doctorFilter.getUnionId() != null){
            Set<MultiJoin> multiJoins = new LinkedHashSet<>();
            multiJoins.add(new MultiJoin("id", "profile", null));
            multiJoins.add(new MultiJoin("id", "union", doctorFilter.getUnionId()));
            filters.add(generateMultiJoinTableFilter(multiJoins, MULTI_JOIN));
        }

        if (doctorFilter.getUnionIds() != null && !doctorFilter.getUnionIds().isEmpty()) {
            Set<MultiJoinIn<?>> joins = new LinkedHashSet<>();
            joins.add(new MultiJoinIn<>("id", "profile", "id", null));
            joins.add(new MultiJoinIn<>("id", "union", "id", doctorFilter.getUnionIds()));
            filters.add(generateMultiJoinTableInFilter(joins, MULTI_JOIN_IN));
        }

        return filters;
    }

    private void checkDoctorDependencies(Long doctorId) {
        List<String> dependencies = new ArrayList<>();

        long degreeCount = doctorDegreeRepository.countByDoctorId(doctorId);
        if (degreeCount > 0) {
            dependencies.add("Doctor Degree Count: " + degreeCount);
        }

        long workplaceCount = doctorWorkplaceRepository.countByDoctorId(doctorId);
        if (workplaceCount > 0) {
            dependencies.add("Doctor Workplace Count: " + workplaceCount);
        }

        long associationCount = doctorAssociationRepository.countByDoctorId(doctorId);
        if (associationCount > 0) {
            dependencies.add("Doctor Association Count: " + associationCount);
        }

        long scheduleCount = doctorScheduleRepository.countDoctorScheduleByDoctorWorkplace_Doctor_Id(doctorId);
        if (scheduleCount > 0) {
            dependencies.add("Doctor Schedule Count: " + scheduleCount);
        }

        if (!dependencies.isEmpty()) {
            throw new UnprocessableEntityException(
                    String.format("Doctor with ID %d cannot be deleted because it is used in the following tables: %s",
                            doctorId, String.join(", ", dependencies))
            );
        }
    }
}