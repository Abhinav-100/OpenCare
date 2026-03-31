package com.ciphertext.opencarebackend.modules.catalog.controller;

import com.ciphertext.opencarebackend.modules.blood.dto.response.enums.BloodComponentResponse;
import com.ciphertext.opencarebackend.modules.blood.dto.response.enums.BloodDonationBadgeResponse;
import com.ciphertext.opencarebackend.modules.blood.dto.response.enums.BloodGroupResponse;
import com.ciphertext.opencarebackend.modules.blood.dto.response.enums.RequisitionStatusResponse;
import com.ciphertext.opencarebackend.modules.catalog.dto.response.enums.CountryResponse;
import com.ciphertext.opencarebackend.modules.catalog.dto.response.enums.DomainResponse;
import com.ciphertext.opencarebackend.modules.payment.dto.response.enums.ContributionActionResponse;
import com.ciphertext.opencarebackend.modules.payment.dto.response.enums.ContributionBadgeResponse;
import com.ciphertext.opencarebackend.modules.provider.dto.response.enums.AmbulanceTypeResponse;
import com.ciphertext.opencarebackend.modules.provider.dto.response.enums.AssociationTypeResponse;
import com.ciphertext.opencarebackend.modules.provider.dto.response.enums.DegreeTypeResponse;
import com.ciphertext.opencarebackend.modules.provider.dto.response.enums.DoctorBadgeResponse;
import com.ciphertext.opencarebackend.modules.provider.dto.response.enums.HospitalAmenityTypeResponse;
import com.ciphertext.opencarebackend.modules.provider.dto.response.enums.HospitalTypeResponse;
import com.ciphertext.opencarebackend.modules.provider.dto.response.enums.InstitutionTypeResponse;
import com.ciphertext.opencarebackend.modules.provider.dto.response.enums.SocialOrganizationTypeResponse;
import com.ciphertext.opencarebackend.modules.shared.dto.response.enums.AgeGroupResponse;
import com.ciphertext.opencarebackend.modules.shared.dto.response.enums.CampaignTypeResponse;
import com.ciphertext.opencarebackend.modules.shared.dto.response.enums.DaysOfWeekResponse;
import com.ciphertext.opencarebackend.modules.shared.dto.response.enums.DocumentTypeResponse;
import com.ciphertext.opencarebackend.modules.shared.dto.response.enums.EquipmentCategoryResponse;
import com.ciphertext.opencarebackend.modules.shared.dto.response.enums.GenderResponse;
import com.ciphertext.opencarebackend.modules.shared.dto.response.enums.MembershipTypeResponse;
import com.ciphertext.opencarebackend.modules.shared.dto.response.enums.OrganizationTypeResponse;
import com.ciphertext.opencarebackend.modules.shared.dto.response.enums.PermissionResponse;
import com.ciphertext.opencarebackend.modules.shared.dto.response.enums.PositionResponse;
import com.ciphertext.opencarebackend.modules.shared.dto.response.enums.TeacherPositionResponse;
import com.ciphertext.opencarebackend.modules.user.dto.response.enums.UserTypeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Arrays;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ciphertext.opencarebackend.enums.AgeGroup;
import com.ciphertext.opencarebackend.enums.AmbulanceType;
import com.ciphertext.opencarebackend.enums.AssociationType;
import com.ciphertext.opencarebackend.enums.BloodComponent;
import com.ciphertext.opencarebackend.enums.BloodDonationBadge;
import com.ciphertext.opencarebackend.enums.BloodGroup;
import com.ciphertext.opencarebackend.enums.CampaignType;
import com.ciphertext.opencarebackend.enums.ContributionAction;
import com.ciphertext.opencarebackend.enums.ContributionBadge;
import com.ciphertext.opencarebackend.enums.Country;
import com.ciphertext.opencarebackend.enums.DaysOfWeek;
import com.ciphertext.opencarebackend.enums.DegreeType;
import com.ciphertext.opencarebackend.enums.DoctorBadge;
import com.ciphertext.opencarebackend.enums.DocumentType;
import com.ciphertext.opencarebackend.enums.Domain;
import com.ciphertext.opencarebackend.enums.EquipmentCategory;
import com.ciphertext.opencarebackend.enums.Gender;
import com.ciphertext.opencarebackend.enums.HospitalAmenityType;
import com.ciphertext.opencarebackend.enums.HospitalType;
import com.ciphertext.opencarebackend.enums.InstitutionType;
import com.ciphertext.opencarebackend.enums.MembershipType;
import com.ciphertext.opencarebackend.enums.OrganizationType;
import com.ciphertext.opencarebackend.enums.Permission;
import com.ciphertext.opencarebackend.enums.Position;
import com.ciphertext.opencarebackend.enums.RequisitionStatus;
import com.ciphertext.opencarebackend.enums.SocialOrganizationType;
import com.ciphertext.opencarebackend.enums.TeacherPosition;
import com.ciphertext.opencarebackend.enums.UserType;




@RestController
@RequestMapping("/api")
@Tag(name = "Enum Management", description = "API for retrieving enum values used throughout the application")
public class EnumApiController {

    @Operation(summary = "Get all age groups", description = "Returns all possible age groups.")
    @GetMapping("/age-groups")
    public List<AgeGroupResponse> getAllAgeGroups() {
        return Arrays.stream(AgeGroup.values())
                .map(AgeGroup::toResponse)
                .toList();
    }

    @Operation(summary = "Get all ambulance types", description = "Returns all possible ambulance types.")
    @GetMapping("/ambulance-types")
    public List<AmbulanceTypeResponse> getAllAmbulanceTypes() {
        return Arrays.stream(AmbulanceType.values())
                .map(AmbulanceType::toResponse)
                .toList();
    }

    @Operation(summary = "Get all association types", description = "Returns all possible association types.")
    @GetMapping("/association-types")
    public List<AssociationTypeResponse> getAllAssociationTypes() {
        return Arrays.stream(AssociationType.values())
                .map(AssociationType::toResponse)
                .toList();
    }

    @Operation(summary = "Get all blood components", description = "Returns all possible blood components.")
    @GetMapping("/blood-components")
    public List<BloodComponentResponse> getAllBloodComponents() {
        return Arrays.stream(BloodComponent.values())
                .map(BloodComponent::toResponse)
                .toList();
    }

    @Operation(summary = "Get all blood donation badges", description = "Returns all possible blood donation badges.")
    @GetMapping("/blood-donation-badges")
    public List<BloodDonationBadgeResponse> getAllBloodDonationBadges() {
        return Arrays.stream(BloodDonationBadge.values())
                .map(BloodDonationBadge::toResponse)
                .toList();
    }

    @Operation(summary = "Get all blood groups", description = "Returns all possible blood groups.")
    @GetMapping("/blood-groups")
    public List<BloodGroupResponse> getAllBloodGroups() {
        return Arrays.stream(BloodGroup.values())
                .map(BloodGroup::toResponse)
                .toList();
    }

    @Operation(summary = "Get all campaign types", description = "Returns all possible campaign types.")
    @GetMapping("/campaign-types")
    public List<CampaignTypeResponse> getAllCampaignTypes() {
        return Arrays.stream(CampaignType.values())
                .map(CampaignType::toResponse)
                .toList();
    }

    @Operation(summary = "Get all contribution actions", description = "Returns all possible contribution actions.")
    @GetMapping("/contribution-actions")
    public List<ContributionActionResponse> getAllContributionActions() {
        return Arrays.stream(ContributionAction.values())
                .map(ContributionAction::toResponse)
                .toList();
    }

    @Operation(summary = "Get all contribution badges", description = "Returns all possible contribution badges.")
    @GetMapping("/contribution-badges")
    public List<ContributionBadgeResponse> getAllContributionBadges() {
        return Arrays.stream(ContributionBadge.values())
                .map(ContributionBadge::toResponse)
                .toList();
    }

    @Operation(summary = "Get all countries", description = "Returns all possible countries.")
    @GetMapping("countries")
    public List<CountryResponse> getAllCountries() {
        return Arrays.stream(Country.values())
                .map(Country::toResponse)
                .toList();
    }

    @Operation(summary = "Get all days of week", description = "Returns all days of the week.")
    @GetMapping("/days-of-week")
    public List<DaysOfWeekResponse> getAllDaysOfWeek() {
        return Arrays.stream(DaysOfWeek.values())
                .map(DaysOfWeek::toResponse)
                .toList();
    }

    @Operation(summary = "Get all degree types", description = "Returns all possible degree types.")
    @GetMapping("/degree-types")
    public List<DegreeTypeResponse> getAllDegreeTypes() {
        return Arrays.stream(DegreeType.values())
                .map(DegreeType::toResponse)
                .toList();
    }

    @Operation(summary = "Get all document types", description = "Returns all possible document types.")
    @GetMapping("/document-types")
    public List<DocumentTypeResponse> getAllDocumentTypes() {
        return Arrays.stream(DocumentType.values())
                .map(DocumentType::toResponse)
                .toList();
    }

    @Operation(summary = "Get all doctor badges", description = "Returns all possible doctor badges.")
    @GetMapping("/doctor-badges")
    public List<DoctorBadgeResponse> getAllDoctorBadges() {
        return Arrays.stream(DoctorBadge.values())
                .map(DoctorBadge::toResponse)
                .toList();
    }

    @Operation(summary = "Get all domains", description = "Returns all possible domains.")
    @GetMapping("/domain")
    public List<DomainResponse> getAllDomains() {
        return Arrays.stream(Domain.values())
                .map(Domain::toResponse)
                .toList();
    }

    @Operation(summary = "Get all equipment categories", description = "Returns all possible equipment categories.")
    @GetMapping("/equipment-categories")
    public List<EquipmentCategoryResponse> getAllEquipmentCategories() {
        return Arrays.stream(EquipmentCategory.values())
                .map(EquipmentCategory::toResponse)
                .toList();
    }

    @Operation(summary = "Get all genders", description = "Returns all possible genders.")
    @GetMapping("/gender")
    public List<GenderResponse> getAllGenders() {
        return Arrays.stream(Gender.values())
                .map(Gender::toResponse)
                .toList();
    }

    @Operation(summary = "Get all hospital amenity types", description = "Returns all possible hospital amenity types.")
    @GetMapping("/hospital-amenity-types")
    public List<HospitalAmenityTypeResponse> getAllHospitalAmenityTypes() {
        return Arrays.stream(HospitalAmenityType.values())
                .map(HospitalAmenityType::toResponse)
                .toList();
    }

    @Operation(summary = "Get all hospital types", description = "Returns all possible hospital types.")
    @GetMapping("/hospital-types")
    public List<HospitalTypeResponse> getAllHospitalTypes() {
        return Arrays.stream(HospitalType.values())
                .map(HospitalType::toResponse)
                .toList();
    }

    @Operation(summary = "Get all institution types", description = "Returns all possible institution types.")
    @GetMapping("/institution-types")
    public List<InstitutionTypeResponse> getAllInstitutionTypes() {
        return Arrays.stream(InstitutionType.values())
                .map(InstitutionType::toResponse)
                .toList();
    }

    @Operation(summary = "Get all membership types", description = "Returns all possible membership types.")
    @GetMapping("/membership-types")
    public List<MembershipTypeResponse> getAllMembershipTypes() {
        return Arrays.stream(MembershipType.values())
                .map(MembershipType::toResponse)
                .toList();
    }

    @Operation(summary = "Get all organization types", description = "Returns all possible organization types.")
    @GetMapping("/organization-types")
    public List<OrganizationTypeResponse> getAllOrganizationTypeResponses() {
        return Arrays.stream(OrganizationType.values())
                .map(OrganizationType::toResponse)
                .toList();
    }

    @Operation(summary = "Get all permissions", description = "Returns all possible permissions.")
    @GetMapping("/permissions")
    public List<PermissionResponse> getAllPermissions() {
        return Arrays.stream(Permission.values())
                .map(Permission::toResponse)
                .toList();
    }

    @Operation(summary = "Get all positions", description = "Returns all possible positions.")
    @GetMapping("/positions")
    public List<PositionResponse> getAllPositions() {
        return Arrays.stream(Position.values())
                .map(Position::toResponse)
                .toList();
    }

    @Operation(summary = "Get all requisition statuses", description = "Returns all possible requisition statuses.")
    @GetMapping("/requisition-statuses")
    public List<RequisitionStatusResponse> getAllRequisitionStatusResponses() {
        return Arrays.stream(RequisitionStatus.values())
                .map(RequisitionStatus::toResponse)
                .toList();
    }

    @Operation(summary = "Get all social organization types", description = "Returns all possible social organization types.")
    @GetMapping("/social-organization-types")
    public List<SocialOrganizationTypeResponse> getAllSocialOrganizationTypeResponses() {
        return Arrays.stream(SocialOrganizationType.values())
                .map(SocialOrganizationType::toResponse)
                .toList();
    }

    @Operation(summary = "Get all teacher positions", description = "Returns all possible teacher positions.")
    @GetMapping("/teacher-positions")
    public List<TeacherPositionResponse> getAllTeacherPositions() {
        return Arrays.stream(TeacherPosition.values())
                .map(TeacherPosition::toResponse)
                .toList();
    }

    @Operation(summary = "Get all user types", description = "Returns all possible user types.")
    @GetMapping("/user-types")
    public List<UserTypeResponse> getAllUserTypes() {
        return Arrays.stream(UserType.values())
                .map(UserType::toResponse)
                .toList();
    }
}