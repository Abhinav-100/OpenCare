package com.ciphertext.opencarebackend.modules.user.dto.filter;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ProfileFilter {
    private String name;
    private String phone;
    private String email;
    private String userType;
    private String gender;
    private Integer minAge;
    private Integer maxAge;
    private String bloodGroup;
    private Integer districtId;
    private Integer upazilaId;
    private Integer unionId;
}
