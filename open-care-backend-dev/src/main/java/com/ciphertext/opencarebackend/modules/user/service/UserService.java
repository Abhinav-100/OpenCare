package com.ciphertext.opencarebackend.modules.user.service;
import com.ciphertext.opencarebackend.modules.user.dto.request.DomainUserRequest;
public interface UserService {
    void assignUserToDomains(Long profileId, DomainUserRequest domainUserRequest);

    void removeUserFromDomains(Long profileId, DomainUserRequest domainUserRequest);
}
