package com.ciphertext.opencarebackend.mapper;
import com.ciphertext.opencarebackend.modules.blood.dto.response.BloodDonorResponse;
import com.ciphertext.opencarebackend.entity.Profile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BloodDonorMapper {

    @Mapping(target = "gender", expression = "java(profile.getGender() != null ? profile.getGender().toResponse() : null)")
    @Mapping(target = "bloodGroup", expression = "java(profile.getBloodGroup() != null ? profile.getBloodGroup().toResponse() : null)")
    @Mapping(target = "district", source = "district")
    @Mapping(target = "upazila", source = "upazila")
    @Mapping(target = "union", source = "union")
    BloodDonorResponse toResponse(Profile profile);
}
