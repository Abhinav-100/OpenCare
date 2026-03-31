package com.ciphertext.opencarebackend.mapper;
import com.ciphertext.opencarebackend.modules.provider.dto.request.HospitalMedicalTestRequest;
import com.ciphertext.opencarebackend.modules.provider.dto.response.HospitalMedicalTestResponse;
import com.ciphertext.opencarebackend.entity.HospitalMedicalTest;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
@Component
public interface HospitalMedicalTestMapper {

    @Mapping(source ="medicalTest.name",target = "name")
    @Mapping(source = "testCode",target = "testCode")
    @Mapping(source = "deliveryTime",target = "deliveryTime")
    HospitalMedicalTestResponse toResponse(HospitalMedicalTest hospitalMedicalTest);

    @Mapping(source = "hospitalId", target = "hospital.id")
    @Mapping(source = "medicalTestId", target = "medicalTestId")
    HospitalMedicalTest toEntity(HospitalMedicalTestRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(HospitalMedicalTestRequest request, @MappingTarget HospitalMedicalTest hospitalMedicalTestDegree);

}
