package com.ciphertext.opencarebackend.modules.auth.service;
import com.ciphertext.opencarebackend.modules.auth.dto.request.RegistrationRequest;
import com.ciphertext.opencarebackend.modules.auth.dto.request.DoctorSelfRegistrationRequest;
import com.ciphertext.opencarebackend.modules.auth.dto.response.RegistrationResponse;
public interface AuthService {
    /**
     * Registers a new user with the provided registration request.
     *
     * @param registrationRequest the registration request containing user details
     * @return a RegistrationResponse containing the result of the registration
     */
    RegistrationResponse registerUser(RegistrationRequest registrationRequest);

    /**
     * Registers a doctor through self-onboarding.
     * New doctors are created in pending approval state.
     *
     * @param registrationRequest doctor self-registration payload
     * @return a RegistrationResponse containing the result of the registration
     */
    RegistrationResponse registerDoctor(DoctorSelfRegistrationRequest registrationRequest);
}
