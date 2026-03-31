package com.ciphertext.opencarebackend.mapper;
import com.ciphertext.opencarebackend.modules.provider.dto.request.HospitalRequest;
import com.ciphertext.opencarebackend.modules.provider.dto.response.HospitalResponse;
import com.ciphertext.opencarebackend.modules.provider.dto.response.enums.HospitalTypeResponse;
import com.ciphertext.opencarebackend.modules.shared.dto.response.enums.OrganizationTypeResponse;
import com.ciphertext.opencarebackend.entity.Hospital;
import com.ciphertext.opencarebackend.enums.HospitalType;
import com.ciphertext.opencarebackend.enums.OrganizationType;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    uses = {DistrictMapper.class, UpazilaMapper.class, UnionMapper.class, TagMapper.class}
)
@Component
public interface HospitalMapper {

    @Mapping(source = "hospitalType", target = "hospitalType", qualifiedByName = "hospitalEnumToResponse")
    @Mapping(source = "organizationType", target = "organizationType", qualifiedByName = "organizationEnumToResponse")
    @Mapping(source = "district", target = "district")
    @Mapping(source = "upazila", target = "upazila")
    @Mapping(source = "union", target = "union")
    @Mapping(source = "tags", target = "tags")
    HospitalResponse toResponse(Hospital hospital);

    @Mapping(source = "hospitalType", target = "hospitalType", qualifiedByName = "hospitalStringToEnum")
    @Mapping(source = "organizationType", target = "organizationType", qualifiedByName = "organizationStringToEnum")
    @Mapping(source = "districtId", target = "district.id", conditionQualifiedByName = "isNotNull")
    @Mapping(source = "upazilaId", target = "upazila.id", conditionQualifiedByName = "isNotNull")
    @Mapping(source = "unionId", target = "union.id", conditionQualifiedByName = "isNotNull")
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "hasEmergencyService", defaultValue = "false")
    @Mapping(target = "hasAmbulanceService", defaultValue = "false")
    @Mapping(target = "hasBloodBank", defaultValue = "false")
    @Mapping(target = "isAffiliated", defaultValue = "false")
    @Mapping(target = "isActive", defaultValue = "true")
    Hospital toEntity(HospitalRequest request);

    @BeanMapping(
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
    )
    @Mapping(source = "hospitalType", target = "hospitalType", qualifiedByName = "hospitalStringToEnum")
    @Mapping(source = "organizationType", target = "organizationType", qualifiedByName = "organizationStringToEnum")
    @Mapping(source = "districtId", target = "district.id", conditionQualifiedByName = "isNotNull")
    @Mapping(source = "upazilaId", target = "upazila.id", conditionQualifiedByName = "isNotNull")
    @Mapping(source = "unionId", target = "union.id", conditionQualifiedByName = "isNotNull")
    @Mapping(target = "tags", ignore = true)
    void partialUpdate(HospitalRequest request, @MappingTarget Hospital hospital);

    @Named("isNotNull")
    default boolean isNotNull(Integer id) {
        return id != null;
    }


    @Named("hospitalEnumToResponse")
    default HospitalTypeResponse hospitalEnumToResponse(HospitalType hospitalType) {
        return hospitalType != null ? hospitalType.toResponse() : null;
    }

    @Named("hospitalStringToEnum")
    default HospitalType hospitalStringToEnum(String hospitalType) {
        return hospitalType != null ? HospitalType.valueOf(hospitalType) : null;
    }

    @Named("organizationEnumToResponse")
    default OrganizationTypeResponse organizationEnumToResponse(OrganizationType organizationType) {
        return organizationType != null ? organizationType.toResponse() : null;
    }

    @Named("organizationStringToEnum")
    default OrganizationType organizationStringToEnum(String organizationType) {
        return organizationType != null ? OrganizationType.valueOf(organizationType) : null;
    }
}
