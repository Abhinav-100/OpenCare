package com.ciphertext.opencarebackend.modules.auth.service.impl;
import com.ciphertext.opencarebackend.modules.auth.dto.request.KeycloakRegistrationRequest;
import com.ciphertext.opencarebackend.modules.auth.dto.request.DoctorSelfRegistrationRequest;
import com.ciphertext.opencarebackend.modules.auth.dto.request.RegistrationRequest;
import com.ciphertext.opencarebackend.modules.auth.dto.response.RegistrationResponse;
import com.ciphertext.opencarebackend.entity.Doctor;
import com.ciphertext.opencarebackend.entity.District;
import com.ciphertext.opencarebackend.entity.Hospital;
import com.ciphertext.opencarebackend.entity.Profile;
import com.ciphertext.opencarebackend.enums.BloodGroup;
import com.ciphertext.opencarebackend.enums.Gender;
import com.ciphertext.opencarebackend.enums.UserType;
import com.ciphertext.opencarebackend.exception.BadRequestException;
import com.ciphertext.opencarebackend.exception.DuplicateResourceException;
import com.ciphertext.opencarebackend.exception.KeycloakServerException;
import com.ciphertext.opencarebackend.modules.provider.repository.DoctorRepository;
import com.ciphertext.opencarebackend.modules.shared.repository.DistrictRepository;
import com.ciphertext.opencarebackend.modules.provider.repository.HospitalRepository;
import com.ciphertext.opencarebackend.modules.user.repository.ProfileRepository;
import com.ciphertext.opencarebackend.modules.auth.service.AuthService;
import com.ciphertext.opencarebackend.modules.auth.service.KeycloakService;
import com.ciphertext.opencarebackend.modules.user.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final KeycloakService keycloakService;
    private final ProfileService profileService;
    private final DistrictRepository districtRepository;
    private final ProfileRepository profileRepository;
    private final DoctorRepository doctorRepository;
    private final HospitalRepository hospitalRepository;

    @Override
    @Transactional
    public RegistrationResponse registerUser(RegistrationRequest registrationRequest) {
        log.info("Attempting to register user with email: {}", registrationRequest.getEmail());

        // Check if user already exists
        if (profileRepository.findByEmail(registrationRequest.getEmail()).isPresent()) {
            log.warn("Registration failed: Email already exists - {}", registrationRequest.getEmail());
            throw new DuplicateResourceException("A user with this email already exists");
        }

        if (profileRepository.findByUsername(registrationRequest.getEmail()).isPresent()) {
            log.warn("Registration failed: Username already exists - {}", registrationRequest.getEmail());
            throw new DuplicateResourceException("A user with this username already exists");
        }

        // Validate district exists
        District district = districtRepository.findById(registrationRequest.getDistrictId())
                .orElseThrow(() -> new BadRequestException("Invalid district ID: " + registrationRequest.getDistrictId()));

        try {
            // Create Keycloak user
            List<KeycloakRegistrationRequest.Credential> credentials = new ArrayList<>();
            KeycloakRegistrationRequest.Credential credential = new KeycloakRegistrationRequest.Credential();
            credential.setType("password");
            credential.setValue(registrationRequest.getPassword());
            credential.setTemporary(false);
            credentials.add(credential);

            KeycloakRegistrationRequest keycloakRequest =
                    KeycloakRegistrationRequest.builder()
                            .email(registrationRequest.getEmail())
                            .firstName(registrationRequest.getFirstName())
                            .lastName(registrationRequest.getLastName())
                            .username(registrationRequest.getEmail())
                            .enabled(true)
                            .credentials(credentials)
                            .build();

            String userId = String.valueOf(keycloakService.registerUser(
                    keycloakRequest,
                    UserType.USER.getKeycloakGroupName()
            ).block());

            if (userId == null || userId.equals("null") || userId.isBlank()) {
                log.error("Failed to register user in Keycloak");
                throw new KeycloakServerException("Failed to create user in authentication system");
            }

            // Create profile
            Profile profile = new Profile();
            profile.setName(registrationRequest.getFirstName() + " " + registrationRequest.getLastName());
            profile.setBnName(registrationRequest.getFirstName() + " " + registrationRequest.getLastName());
            profile.setUserType(UserType.USER);
            profile.setEmail(registrationRequest.getEmail());
            profile.setUsername(registrationRequest.getEmail());
            profile.setPhone(registrationRequest.getPhone());
            profile.setDistrict(district);
            profile.setBloodGroup(BloodGroup.valueOf(registrationRequest.getBloodGroup()));
            profile.setGender(Gender.valueOf(registrationRequest.getGender()));
            profile.setKeycloakUserId(userId);

            profileService.createProfile(profile);

            log.info("User registered successfully with ID: {}", userId);

            return RegistrationResponse.builder()
                    .message("User registered successfully")
                    .userId(userId)
                    .status("SUCCESS")
                    .build();

        } catch (IllegalArgumentException | BadRequestException | DuplicateResourceException e) {
            log.error("Registration validation error: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    @Transactional
    public RegistrationResponse registerDoctor(DoctorSelfRegistrationRequest registrationRequest) {
        log.info("Attempting doctor self-registration for email: {}", registrationRequest.getEmail());

        // Check duplicates across profile and doctor domains
        if (profileRepository.findByEmail(registrationRequest.getEmail()).isPresent()) {
            throw new DuplicateResourceException("A user with this email already exists");
        }
        if (profileRepository.findByUsername(registrationRequest.getEmail()).isPresent()) {
            throw new DuplicateResourceException("A user with this username already exists");
        }
        if (registrationRequest.getBmdcNo() != null && doctorRepository.existsByBmdcNo(registrationRequest.getBmdcNo())) {
            throw new DuplicateResourceException("Doctor with BMDC number " + registrationRequest.getBmdcNo() + " already exists");
        }

        District district = districtRepository.findById(registrationRequest.getDistrictId())
                .orElseThrow(() -> new BadRequestException("Invalid district ID: " + registrationRequest.getDistrictId()));

        Hospital hospital = null;
        if (registrationRequest.getHospitalId() != null) {
            hospital = hospitalRepository.findById(registrationRequest.getHospitalId())
                    .orElseThrow(() -> new BadRequestException("Invalid hospital ID: " + registrationRequest.getHospitalId()));
        }

        List<KeycloakRegistrationRequest.Credential> credentials = new ArrayList<>();
        KeycloakRegistrationRequest.Credential credential = new KeycloakRegistrationRequest.Credential();
        credential.setType("password");
        credential.setValue(registrationRequest.getPassword());
        credential.setTemporary(false);
        credentials.add(credential);

        KeycloakRegistrationRequest keycloakRequest = KeycloakRegistrationRequest.builder()
                .email(registrationRequest.getEmail())
                .firstName(registrationRequest.getFirstName())
                .lastName(registrationRequest.getLastName())
                .username(registrationRequest.getEmail())
                .enabled(true)
                .credentials(credentials)
                .build();

        String userId = String.valueOf(keycloakService.registerUser(
                keycloakRequest,
                UserType.DOCTOR.getKeycloakGroupName()
        ).block());

        if (userId == null || userId.equals("null") || userId.isBlank()) {
            throw new KeycloakServerException("Failed to create doctor user in authentication system");
        }

        Profile profile = new Profile();
        profile.setName(registrationRequest.getFirstName() + " " + registrationRequest.getLastName());
        profile.setBnName(registrationRequest.getFirstName() + " " + registrationRequest.getLastName());
        profile.setUserType(UserType.DOCTOR);
        profile.setEmail(registrationRequest.getEmail());
        profile.setUsername(registrationRequest.getEmail());
        profile.setPhone(registrationRequest.getPhone());
        profile.setDistrict(district);
        profile.setBloodGroup(BloodGroup.valueOf(registrationRequest.getBloodGroup()));
        profile.setGender(Gender.valueOf(registrationRequest.getGender()));
        profile.setKeycloakUserId(userId);

        Profile savedProfile = profileService.createProfile(profile);

        Doctor doctor = new Doctor();
        doctor.setProfile(savedProfile);
        doctor.setBmdcNo(registrationRequest.getBmdcNo());
        doctor.setDegrees(registrationRequest.getDegrees());
        doctor.setSpecializations(registrationRequest.getSpecializations());
        doctor.setDescription(registrationRequest.getDescription());
        doctor.setConsultationFeeOnline(registrationRequest.getConsultationFeeOnline());
        doctor.setConsultationFeeOffline(registrationRequest.getConsultationFeeOffline());
        doctor.setHospital(hospital);

        // Pending by default; admin approval will activate and verify.
        doctor.setIsVerified(false);
        doctor.setIsActive(false);

        doctorRepository.save(doctor);

        return RegistrationResponse.builder()
                .message("Doctor registered successfully and is pending admin approval")
                .userId(userId)
                .status("PENDING_APPROVAL")
                .build();
    }
}
