package com.ciphertext.opencarebackend.mapper;
import com.ciphertext.opencarebackend.modules.provider.dto.request.AssociationRequest;
import com.ciphertext.opencarebackend.modules.provider.dto.response.AssociationResponse;
import com.ciphertext.opencarebackend.modules.provider.dto.response.enums.AssociationTypeResponse;
import com.ciphertext.opencarebackend.modules.catalog.dto.response.enums.CountryResponse;
import com.ciphertext.opencarebackend.modules.catalog.dto.response.enums.DomainResponse;
import com.ciphertext.opencarebackend.entity.Association;
import com.ciphertext.opencarebackend.entity.District;
import com.ciphertext.opencarebackend.entity.Division;
import com.ciphertext.opencarebackend.entity.MedicalSpeciality;
import com.ciphertext.opencarebackend.entity.Upazila;
import com.ciphertext.opencarebackend.enums.AssociationType;
import com.ciphertext.opencarebackend.enums.Country;
import com.ciphertext.opencarebackend.enums.Domain;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
@Component
public interface AssociationMapper {

    @Mapping(source = "medicalSpeciality", target = "medicalSpeciality")
    @Mapping(source = "division", target = "division")
    @Mapping(source = "division.id", target = "divisionId")
    @Mapping(source = "district", target = "district")
    @Mapping(source = "district.id", target = "districtId")
    @Mapping(source = "upazila", target = "upazila")
    @Mapping(source = "upazila.id", target = "upazilaId")
    @Mapping(source = "originCountry", target = "originCountry", qualifiedByName = "countryEnumToResponse")
    @Mapping(source = "associationType", target = "associationType", qualifiedByName = "associationTypeEnumToResponse")
    @Mapping(source = "domain", target = "domain", qualifiedByName = "domainEnumToResponse")
    AssociationResponse toResponse(Association association);

    @Mapping(source = "medicalSpecialityId", target = "medicalSpeciality", qualifiedByName = "medicalSpecialityFromId")
    @Mapping(source = "divisionId", target = "division", qualifiedByName = "divisionFromId")
    @Mapping(source = "districtId", target = "district", qualifiedByName = "districtFromId")
    @Mapping(source = "upazilaId", target = "upazila", qualifiedByName = "upazilaFromId")
    @Mapping(source = "originCountry", target = "originCountry", qualifiedByName = "countryStringToEnum")
    @Mapping(source = "associationType", target = "associationType", qualifiedByName = "associationTypeStringToEnum")
    @Mapping(source = "domain", target = "domain", qualifiedByName = "domainStringToEnum")
    Association toEntity(AssociationRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "medicalSpecialityId", target = "medicalSpeciality", qualifiedByName = "medicalSpecialityFromId")
    @Mapping(source = "divisionId", target = "division", qualifiedByName = "divisionFromId")
    @Mapping(source = "districtId", target = "district", qualifiedByName = "districtFromId")
    @Mapping(source = "upazilaId", target = "upazila", qualifiedByName = "upazilaFromId")
    @Mapping(source = "originCountry", target = "originCountry", qualifiedByName = "countryStringToEnum")
    @Mapping(source = "associationType", target = "associationType", qualifiedByName = "associationTypeStringToEnum")
    @Mapping(source = "domain", target = "domain", qualifiedByName = "domainStringToEnum")
    void partialUpdate(AssociationRequest request, @MappingTarget Association association);

    @Named("countryStringToEnum")
    default Country countryStringToEnum(String country) {
        return country != null ? Country.valueOf(country) : Country.INDIA;
    }

    @Named("countryEnumToResponse")
    default CountryResponse countryEnumToResponse(Country country) {
        return country != null ? country.toResponse() : null;
    }

    @Named("associationTypeStringToEnum")
    default AssociationType associationTypeStringToEnum(String associationType) {
        return associationType != null ? AssociationType.valueOf(associationType) : null;
    }

    @Named("associationTypeEnumToResponse")
    default AssociationTypeResponse associationTypeEnumToResponse(AssociationType associationType) {
        return associationType != null ? associationType.toResponse() : null;
    }

    @Named("domainStringToEnum")
    default Domain domainStringToEnum(String domain) {
        return domain != null ? Domain.valueOf(domain) : null;
    }

    @Named("domainEnumToResponse")
    default DomainResponse domainEnumToResponse(Domain domain) {
        return domain != null ? domain.toResponse() : null;
    }

    @Named("medicalSpecialityFromId")
    default MedicalSpeciality medicalSpecialityFromId(Integer id) {
        if (id == null) {
            return null;
        }
        MedicalSpeciality medicalSpeciality = new MedicalSpeciality();
        medicalSpeciality.setId(id);
        return medicalSpeciality;
    }

    @Named("divisionFromId")
    default Division divisionFromId(Integer id) {
        if (id == null) {
            return null;
        }
        Division division = new Division();
        division.setId(id);
        return division;
    }

    @Named("districtFromId")
    default District districtFromId(Integer id) {
        if (id == null) {
            return null;
        }
        District district = new District();
        district.setId(id);
        return district;
    }

    @Named("upazilaFromId")
    default Upazila upazilaFromId(Integer id) {
        if (id == null) {
            return null;
        }
        Upazila upazila = new Upazila();
        upazila.setId(id);
        return upazila;
    }
}
