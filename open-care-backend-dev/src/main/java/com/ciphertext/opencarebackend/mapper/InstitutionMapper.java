package com.ciphertext.opencarebackend.mapper;
import com.ciphertext.opencarebackend.modules.provider.dto.request.InstitutionRequest;
import com.ciphertext.opencarebackend.modules.provider.dto.response.InstitutionResponse;
import com.ciphertext.opencarebackend.modules.catalog.dto.response.enums.CountryResponse;
import com.ciphertext.opencarebackend.modules.provider.dto.response.enums.InstitutionTypeResponse;
import com.ciphertext.opencarebackend.modules.shared.dto.response.enums.OrganizationTypeResponse;
import com.ciphertext.opencarebackend.entity.Institution;
import com.ciphertext.opencarebackend.entity.Tag;
import com.ciphertext.opencarebackend.enums.Country;
import com.ciphertext.opencarebackend.enums.InstitutionType;
import com.ciphertext.opencarebackend.enums.OrganizationType;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {TagMapper.class})
@Component
public interface InstitutionMapper {
    @Mapping(source = "country", target = "country", qualifiedByName = "countryEnumToResponse")
    @Mapping(source = "institutionType", target = "institutionType", qualifiedByName = "hospitalEnumToResponse")
    @Mapping(source = "organizationType", target = "organizationType", qualifiedByName = "organizationEnumToResponse")
    @Mapping(source = "tags", target = "tags")
    InstitutionResponse toResponse(Institution institution);

    @Mapping(source = "country", target = "country", qualifiedByName = "countryStringToEnum")
    @Mapping(source = "institutionType", target = "institutionType", qualifiedByName = "hospitalStringToEnum")
    @Mapping(source = "organizationType", target = "organizationType", qualifiedByName = "organizationStringToEnum")
    @Mapping(source = "districtId", target = "district.id")
    @Mapping(source = "upazilaId", target = "upazila.id")
    @Mapping(source = "affiliatedHospitalId", target = "affiliatedHospital.id")
    @Mapping(source = "tagIds", target = "tags", qualifiedByName = "tagIdsToTags")
    Institution toEntity(InstitutionRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "tagIds", target = "tags", qualifiedByName = "tagIdsToTags")
    void partialUpdate(InstitutionRequest request, @MappingTarget Institution institution);

    @Named("countryStringToEnum")
    default Country countryStringToEnum(String country) {
        return country != null ? Country.valueOf(country) : null;
    }

    @Named("countryEnumToResponse")
    default CountryResponse countryEnumToResponse(Country country) {
        return country != null ? country.toResponse() : null;
    }

    @Named("hospitalEnumToResponse")
    default InstitutionTypeResponse hospitalEnumToResponse(InstitutionType institutionType) {
        return institutionType != null ? institutionType.toResponse() : null;
    }

    @Named("hospitalStringToEnum")
    default InstitutionType hospitalStringToEnum(String institutionType) {
        return institutionType != null ? InstitutionType.valueOf(institutionType) : null;
    }

    @Named("organizationEnumToResponse")
    default OrganizationTypeResponse organizationEnumToResponse(OrganizationType organizationType) {
        return organizationType != null ? organizationType.toResponse() : null;
    }

    @Named("organizationStringToEnum")
    default OrganizationType organizationStringToEnum(String organizationType) {
        return organizationType != null ? OrganizationType.valueOf(organizationType) : null;
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
}
