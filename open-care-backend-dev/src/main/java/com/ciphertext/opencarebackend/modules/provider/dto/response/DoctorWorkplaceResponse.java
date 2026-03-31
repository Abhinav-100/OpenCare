package com.ciphertext.opencarebackend.modules.provider.dto.response;
import com.ciphertext.opencarebackend.modules.shared.dto.response.enums.TeacherPositionResponse;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class DoctorWorkplaceResponse {
    private Long id;
    private DoctorResponse doctor;
    private String doctorPosition;
    private TeacherPositionResponse teacherPosition;
    private MedicalSpecialityResponse medicalSpeciality;
    private InstitutionResponse institution;
    private HospitalResponse hospital;
    private LocalDate startDate;
    private LocalDate endDate;
}
