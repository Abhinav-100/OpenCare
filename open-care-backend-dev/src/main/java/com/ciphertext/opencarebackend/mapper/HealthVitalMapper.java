package com.ciphertext.opencarebackend.mapper;
import com.ciphertext.opencarebackend.modules.clinical.dto.request.HealthVitalRequest;
import com.ciphertext.opencarebackend.modules.clinical.dto.response.HealthVitalResponse;
import com.ciphertext.opencarebackend.entity.HealthVital;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface HealthVitalMapper {

    HealthVitalResponse toResponse(HealthVital healthVital);

    @Mapping(source = "profileId", target = "profile.id")
    HealthVital toEntity(HealthVitalRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "profile", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void partialUpdate(HealthVitalRequest request, @MappingTarget HealthVital healthVital);
}
