package com.ciphertext.opencarebackend.modules.provider.dto.request;

import lombok.Getter;
import lombok.Setter;
import java.util.Date;
@Getter
@Setter

public class NurseRequest {

        private Long id;
        private String bnmcNo;
        private String startDate;
        private String description;
        private Boolean isVerified;
        private boolean isAffiliated;
        private Boolean isActive;
        private String username;
        private byte[] photo;
        private String phone;
        private String email;
        private String name;
        private String bnName;
        private String gender;
        private Date dateOfBirth;
        private String address;
        private Integer districtId;
        private Integer upazilaId;
        private Integer unionId;

}
