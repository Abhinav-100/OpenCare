package com.ciphertext.opencarebackend.modules.user.controller;
import com.ciphertext.opencarebackend.modules.user.dto.request.DomainUserRequest;
import com.ciphertext.opencarebackend.modules.provider.service.DoctorService;
import com.ciphertext.opencarebackend.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Management", description = "API for user-related operations such as creating logins and assigning domain roles")
public class UserApiController {

    private final DoctorService doctorService;
    private final UserService userService;

    @PostMapping("/doctors/{doctorId}/create-user")
    @Operation(summary = "Create login for a doctor", description = "Creates a Keycloak/user login for the specified doctor and returns the created user ID")
    public ResponseEntity<String> createDoctorLogin(@PathVariable Long doctorId) {
        return ResponseEntity.ok("Doctor user created successfully with ID: " + doctorService.createDoctorUser(doctorId));
    }

    @Operation(summary = "Assign user to domain admin roles", description = "Assigns the specified domains/roles to the user identified by profileId")
    @PostMapping("/{profileId}/assign-user-type")
    public ResponseEntity<String> assignUserToDomainAdmin(@PathVariable Long profileId, @RequestBody DomainUserRequest domainUserRequest) {
        userService.assignUserToDomains(profileId, domainUserRequest);
        return ResponseEntity.ok("User assigned as admin to specified domains.");
    }

    @PostMapping("/{profileId}/remove-user-type")
    @Operation(summary = "Remove user domain admin roles", description = "Removes the specified domain admin roles from the user identified by profileId")
    public ResponseEntity<String> removeUserFromDomainAdmin(@PathVariable Long profileId, @RequestBody DomainUserRequest domainUserRequest) {
        userService.removeUserFromDomains(profileId, domainUserRequest);
        return ResponseEntity.ok("User removed from specified domain admin roles.");
    }
}