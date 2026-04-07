package com.ciphertext.opencarebackend.modules.provider.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
/**
 * Flow note: HospitalMedicalTestResponse belongs to the provider doctor/hospital module.
 * Read this with neighboring controller/service/repository files to trace request flow.
 */
public class HospitalMedicalTestResponse {
    private long id;
    private HospitalResponse hospital;
    private MedicalTestResponse medicalTest;
    private String name;
    private String testCode;
    private String category;
    private String description;
    private Integer price;
    private Boolean isAvailable;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime sampleCollectedTime;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime deliveryTime;
    private Boolean isActive ;
}