package com.ciphertext.opencarebackend.mapper;
import com.ciphertext.opencarebackend.modules.provider.dto.request.DoctorRequest;
import com.ciphertext.opencarebackend.modules.provider.dto.response.DoctorResponse;
import com.ciphertext.opencarebackend.entity.Doctor;
import com.ciphertext.opencarebackend.entity.Hospital;
import com.ciphertext.opencarebackend.entity.Tag;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {TagMapper.class})
@Component
public interface DoctorMapper {

    @Mapping(target = "yearOfExperience", expression = "java(calculateYearsOfExperience(doctor.getStartDate()))")
    @Mapping(source = "tags", target = "tags")
    @Mapping(source = "hospital.id", target = "hospitalId")
    @Mapping(source = "hospital.name", target = "hospitalName")
    DoctorResponse toResponse(Doctor doctor);

    @Mapping(source = "tagIds", target = "tags", qualifiedByName = "tagIdsToTags")
    @Mapping(source = "name", target = "profile.name")
    @Mapping(source = "bnName", target = "profile.bnName")
    @Mapping(source = "gender", target = "profile.gender")
    @Mapping(source = "dateOfBirth", target = "profile.dateOfBirth")
    @Mapping(source = "address", target = "profile.address")
    @Mapping(source = "username", target = "profile.username")
    @Mapping(source = "phone", target = "profile.phone")
    @Mapping(source = "email", target = "profile.email")
    @Mapping(source = "photo", target = "profile.imageUrl")
    @Mapping(source = "isVerified", target = "isVerified", defaultValue = "false")
    @Mapping(source = "isActive", target = "isActive", defaultValue = "true")
    @Mapping(source = "districtId", target = "profile.district", qualifiedByName = "districtIdToDistrict")
    @Mapping(source = "upazilaId", target = "profile.upazila", qualifiedByName = "upazilaIdToUpazila")
    @Mapping(source = "unionId", target = "profile.union", qualifiedByName = "unionIdToUnion")
    @Mapping(source = "hospitalId", target = "hospital", qualifiedByName = "hospitalIdToHospital")
    @Mapping(target = "profile.userType", constant = "DOCTOR")
    @Mapping(source = "bloodGroup", target = "profile.bloodGroup")
    @Mapping(source = "facebookProfileUrl", target = "profile.facebookProfileUrl")
    @Mapping(source = "linkedinProfileUrl", target = "profile.linkedinProfileUrl")
    @Mapping(source = "researchGateProfileUrl", target = "profile.researchGateProfileUrl")
    @Mapping(source = "isVolunteer", target = "profile.isVolunteer")
    @Mapping(source = "healthDataConsent", target = "profile.healthDataConsent")
    Doctor toEntity(DoctorRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "tagIds", target = "tags", qualifiedByName = "tagIdsToTags")
    @Mapping(source = "name", target = "profile.name")
    @Mapping(source = "bnName", target = "profile.bnName")
    @Mapping(source = "gender", target = "profile.gender")
    @Mapping(source = "dateOfBirth", target = "profile.dateOfBirth")
    @Mapping(source = "address", target = "profile.address")
    @Mapping(source = "username", target = "profile.username")
    @Mapping(source = "phone", target = "profile.phone")
    @Mapping(source = "email", target = "profile.email")
    @Mapping(source = "photo", target = "profile.imageUrl")
    @Mapping(source = "districtId", target = "profile.district", qualifiedByName = "districtIdToDistrict")
    @Mapping(source = "upazilaId", target = "profile.upazila", qualifiedByName = "upazilaIdToUpazila")
    @Mapping(source = "unionId", target = "profile.union", qualifiedByName = "unionIdToUnion")
    @Mapping(source = "hospitalId", target = "hospital", qualifiedByName = "hospitalIdToHospital")
    @Mapping(source = "bloodGroup", target = "profile.bloodGroup")
    @Mapping(source = "facebookProfileUrl", target = "profile.facebookProfileUrl")
    @Mapping(source = "linkedinProfileUrl", target = "profile.linkedinProfileUrl")
    @Mapping(source = "researchGateProfileUrl", target = "profile.researchGateProfileUrl")
    @Mapping(source = "isVolunteer", target = "profile.isVolunteer")
    @Mapping(source = "healthDataConsent", target = "profile.healthDataConsent")
    void partialUpdate(DoctorRequest request, @MappingTarget Doctor doctor);

    /**
     * Maps DoctorRequest to Doctor entity for update operations.
     * This method properly handles profile fields and ensures profile object is created if needed.
     */
    @Mapping(source = "tagIds", target = "tags", qualifiedByName = "tagIdsToTags")
    @Mapping(source = "name", target = "profile.name")
    @Mapping(source = "bnName", target = "profile.bnName")
    @Mapping(source = "gender", target = "profile.gender")
    @Mapping(source = "dateOfBirth", target = "profile.dateOfBirth")
    @Mapping(source = "address", target = "profile.address")
    @Mapping(source = "username", target = "profile.username")
    @Mapping(source = "phone", target = "profile.phone")
    @Mapping(source = "email", target = "profile.email")
    @Mapping(source = "photo", target = "profile.imageUrl")
    @Mapping(source = "districtId", target = "profile.district", qualifiedByName = "districtIdToDistrict")
    @Mapping(source = "upazilaId", target = "profile.upazila", qualifiedByName = "upazilaIdToUpazila")
    @Mapping(source = "unionId", target = "profile.union", qualifiedByName = "unionIdToUnion")
    @Mapping(source = "hospitalId", target = "hospital", qualifiedByName = "hospitalIdToHospital")
    @Mapping(target = "profile.userType", constant = "DOCTOR")
    @Mapping(source = "bloodGroup", target = "profile.bloodGroup")
    @Mapping(source = "facebookProfileUrl", target = "profile.facebookProfileUrl")
    @Mapping(source = "linkedinProfileUrl", target = "profile.linkedinProfileUrl")
    @Mapping(source = "researchGateProfileUrl", target = "profile.researchGateProfileUrl")
    @Mapping(source = "isVolunteer", target = "profile.isVolunteer")
    @Mapping(source = "healthDataConsent", target = "profile.healthDataConsent")
    Doctor toEntityForUpdate(DoctorRequest request);

    default Integer calculateYearsOfExperience(LocalDate startDate) {
        if (startDate == null) {
            return null;
        }
        return java.time.Period.between(startDate, java.time.LocalDate.now()).getYears();
    }

    @Named("tagIdsToTags")
    default Set<Tag> tagIdsToTags(Set<Integer> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return null;
        }
        return tagIds.stream()
                .map(id -> {
                    Tag tag = new Tag();
                    tag.setId(id);
                    return tag;
                })
                .collect(Collectors.toSet());
    }

    @Named("districtIdToDistrict")
    default com.ciphertext.opencarebackend.entity.District districtIdToDistrict(Integer districtId) {
        if (districtId == null) {
            return null;
        }
        com.ciphertext.opencarebackend.entity.District district = new com.ciphertext.opencarebackend.entity.District();
        district.setId(districtId);
        return district;
    }

    @Named("upazilaIdToUpazila")
    default com.ciphertext.opencarebackend.entity.Upazila upazilaIdToUpazila(Integer upazilaId) {
        if (upazilaId == null) {
            return null;
        }
        com.ciphertext.opencarebackend.entity.Upazila upazila = new com.ciphertext.opencarebackend.entity.Upazila();
        upazila.setId(upazilaId);
        return upazila;
    }

    @Named("unionIdToUnion")
    default com.ciphertext.opencarebackend.entity.Union unionIdToUnion(Integer unionId) {
        if (unionId == null) {
            return null;
        }
        com.ciphertext.opencarebackend.entity.Union union = new com.ciphertext.opencarebackend.entity.Union();
        union.setId(unionId);
        return union;
    }

    @Named("hospitalIdToHospital")
    default Hospital hospitalIdToHospital(Integer hospitalId) {
        if (hospitalId == null) {
            return null;
        }
        Hospital hospital = new Hospital();
        hospital.setId(hospitalId);
        return hospital;
    }
}