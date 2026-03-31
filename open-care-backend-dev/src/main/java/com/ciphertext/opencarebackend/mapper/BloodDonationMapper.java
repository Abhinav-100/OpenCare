package com.ciphertext.opencarebackend.mapper;
import com.ciphertext.opencarebackend.modules.blood.dto.request.BloodDonationRequest;
import com.ciphertext.opencarebackend.modules.blood.dto.response.BloodDonationResponse;
import com.ciphertext.opencarebackend.entity.BloodDonation;
import com.ciphertext.opencarebackend.enums.BloodComponent;
import com.ciphertext.opencarebackend.enums.BloodGroup;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
@Component
public interface BloodDonationMapper {

    BloodDonationResponse toResponse(BloodDonation bloodDonation);

    @Mapping(source = "donorId", target = "donor.id")
    @Mapping(source = "hospitalId", target = "hospital.id")
    @Mapping(source = "bloodGroup", target = "bloodGroup", qualifiedByName = "mapBloodGroup")
    @Mapping(source = "bloodComponent", target = "bloodComponent", qualifiedByName = "mapBloodComponent")
    BloodDonation toEntity(BloodDonationRequest request);

    @Named("mapBloodGroup")
    default BloodGroup mapBloodGroup(String bloodGroup) {
        return StringUtils.hasText(bloodGroup) ? BloodGroup.valueOf(bloodGroup) : null;
    }

    @Named("mapBloodComponent")
    default BloodComponent mapBloodComponent(String bloodComponent) {
        return StringUtils.hasText(bloodComponent) ? BloodComponent.valueOf(bloodComponent) : null;
    }
}
