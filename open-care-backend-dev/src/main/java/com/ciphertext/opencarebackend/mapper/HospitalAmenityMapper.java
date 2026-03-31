package com.ciphertext.opencarebackend.mapper;
import com.ciphertext.opencarebackend.modules.provider.dto.request.HospitalAmenityRequest;
import com.ciphertext.opencarebackend.modules.provider.dto.response.HospitalAmenityResponse;
import com.ciphertext.opencarebackend.entity.HospitalAmenity;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
@Component
public interface HospitalAmenityMapper {
    HospitalAmenityResponse toResponse(HospitalAmenity hospitalAmenity);

    @Mapping(target = "hospital.id", source = "hospitalId")
    HospitalAmenity toEntity(HospitalAmenityRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(HospitalAmenityRequest request, @MappingTarget HospitalAmenity hospitalAmenity);

}
