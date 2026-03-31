package com.ciphertext.opencarebackend.modules.user.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class DomainUserRequest {
    private String domainId;
    private String userType;
    private String position;
    private LocalDate startDate;
    private LocalDate endDate;
}