package com.ciphertext.opencarebackend.mapper;
import com.ciphertext.opencarebackend.modules.provider.dto.request.DoctorAssociationRequest;
import com.ciphertext.opencarebackend.modules.provider.dto.response.DoctorAssociationResponse;
import com.ciphertext.opencarebackend.entity.DoctorAssociation;
import com.ciphertext.opencarebackend.enums.MembershipType;
import com.ciphertext.opencarebackend.modules.shared.dto.response.enums.MembershipTypeResponse;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
@Component
public interface DoctorAssociationMapper {

    @Mapping(source = "membershipType", target = "membershipType", qualifiedByName = "membershipTypeEnumToResponse")
    DoctorAssociationResponse toResponse(DoctorAssociation doctorAssociation);

    @Mapping(source = "doctorId", target = "doctor.id")
    @Mapping(source = "associationId", target = "association.id")
    @Mapping(source = "membershipType", target = "membershipType", qualifiedByName = "membershipTypeStringToEnum")
    DoctorAssociation toEntity(DoctorAssociationRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "doctorId", target = "doctor.id")
    @Mapping(source = "associationId", target = "association.id")
    @Mapping(source = "membershipType", target = "membershipType", qualifiedByName = "membershipTypeStringToEnum")
    void partialUpdate(DoctorAssociationRequest request, @MappingTarget DoctorAssociation doctorAssociation);

    @Named("membershipTypeStringToEnum")
    default MembershipType membershipTypeStringToEnum(String membershipType) {
        return membershipType != null ? MembershipType.valueOf(membershipType) : null;
    }

    @Named("membershipTypeEnumToResponse")
    default MembershipTypeResponse membershipTypeEnumToResponse(MembershipType membershipType) {
        return membershipType != null ? membershipType.toResponse() : null;
    }
}
