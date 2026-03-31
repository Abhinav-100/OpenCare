package com.ciphertext.opencarebackend.modules.auth.service.impl;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.ciphertext.opencarebackend.entity.Profile;
import com.ciphertext.opencarebackend.exception.DuplicateResourceException;
import com.ciphertext.opencarebackend.modules.auth.dto.request.RegistrationRequest;
import com.ciphertext.opencarebackend.modules.auth.service.KeycloakService;
import com.ciphertext.opencarebackend.modules.provider.repository.DoctorRepository;
import com.ciphertext.opencarebackend.modules.provider.repository.HospitalRepository;
import com.ciphertext.opencarebackend.modules.shared.repository.DistrictRepository;
import com.ciphertext.opencarebackend.modules.user.repository.ProfileRepository;
import com.ciphertext.opencarebackend.modules.user.service.ProfileService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private KeycloakService keycloakService;

    @Mock
    private ProfileService profileService;

    @Mock
    private DistrictRepository districtRepository;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private HospitalRepository hospitalRepository;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void registerUserThrowsWhenEmailAlreadyExists() {
        RegistrationRequest request = RegistrationRequest.builder()
                .email("existing@opencare.test")
                .firstName("Test")
                .lastName("User")
                .phone("9876543210")
                .password("Strong@123")
                .bloodGroup("A_POSITIVE")
                .gender("MALE")
                .districtId(1)
                .build();

        when(profileRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(new Profile()));

        assertThrows(DuplicateResourceException.class, () -> authService.registerUser(request));

        verify(profileRepository).findByEmail(request.getEmail());
        verify(profileRepository, never()).findByUsername(anyString());
        verifyNoInteractions(districtRepository, keycloakService, profileService, doctorRepository, hospitalRepository);
    }
}
