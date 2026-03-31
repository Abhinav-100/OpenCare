package com.ciphertext.opencarebackend.mapper;
import com.ciphertext.opencarebackend.modules.provider.dto.request.SocialOrganizationRequest;
import com.ciphertext.opencarebackend.modules.provider.dto.response.SocialOrganizationResponse;
import com.ciphertext.opencarebackend.modules.catalog.dto.response.enums.CountryResponse;
import com.ciphertext.opencarebackend.modules.provider.dto.response.enums.SocialOrganizationTypeResponse;
import com.ciphertext.opencarebackend.entity.SocialOrganization;
import com.ciphertext.opencarebackend.entity.Tag;
import com.ciphertext.opencarebackend.enums.Country;
import com.ciphertext.opencarebackend.enums.SocialOrganizationType;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {TagMapper.class})
@Component
public interface SocialOrganizationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(source = "originCountry", target = "originCountry", qualifiedByName = "countryStringToEnum")
    @Mapping(source = "socialOrganizationType", target = "socialOrganizationType", qualifiedByName = "socialOrganizationStringToEnum")
    @Mapping(source = "tagIds", target = "tags", qualifiedByName = "tagIdsToTags")
    SocialOrganization toEntity(SocialOrganizationRequest request);

    @Mapping(source = "originCountry", target = "originCountry", qualifiedByName = "countryEnumToResponse")
    @Mapping(source = "socialOrganizationType", target = "socialOrganizationType", qualifiedByName = "socialOrganizationEnumToResponse")
    @Mapping(source = "tags", target = "tags")
    SocialOrganizationResponse toResponse(SocialOrganization socialOrganization);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(source = "tagIds", target = "tags", qualifiedByName = "tagIdsToTags")
    void partialUpdate(SocialOrganizationRequest request, @MappingTarget SocialOrganization socialOrganization);
    
    @Named("countryStringToEnum")
    default Country countryStringToEnum(String country) {
        return country != null ? Country.valueOf(country) : null;
    }

    @Named("countryEnumToResponse")
    default CountryResponse countryEnumToResponse(Country country) {
        return country != null ? country.toResponse() : null;
    }

    @Named("socialOrganizationEnumToResponse")
    default SocialOrganizationTypeResponse socialOrganizationEnumToResponse(SocialOrganizationType socialOrganizationType) {
        return socialOrganizationType != null ? socialOrganizationType.toResponse() : null;
    }

    @Named("socialOrganizationStringToEnum")
    default SocialOrganizationType socialOrganizationStringToEnum(String socialOrganizationType) {
        return socialOrganizationType != null ? SocialOrganizationType.valueOf(socialOrganizationType) : null;
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
