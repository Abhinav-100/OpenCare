package com.ciphertext.opencarebackend.mapper;
import com.ciphertext.opencarebackend.modules.blood.dto.request.BloodRequisitionRequest;
import com.ciphertext.opencarebackend.modules.blood.dto.response.BloodRequisitionResponse;
import com.ciphertext.opencarebackend.entity.District;
import com.ciphertext.opencarebackend.entity.Hospital;
import com.ciphertext.opencarebackend.entity.Profile;
import com.ciphertext.opencarebackend.entity.Upazila;
import com.ciphertext.opencarebackend.enums.BloodComponent;
import com.ciphertext.opencarebackend.enums.BloodGroup;
import com.ciphertext.opencarebackend.enums.Gender;
import com.ciphertext.opencarebackend.enums.RequisitionStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import com.ciphertext.opencarebackend.entity.BloodRequisition;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BloodRequisitionMapper {

    private final ProfileMapper profileMapper;
    private final HospitalMapper hospitalMapper;
    private final DistrictMapper districtMapper;
    private final UpazilaMapper upazilaMapper;

    public BloodRequisitionResponse toResponse(BloodRequisition bloodRequisition) {
        if (bloodRequisition == null) {
            return null;
        }

        BloodRequisitionResponse response = new BloodRequisitionResponse();
        response.setId(bloodRequisition.getId());
        response.setRequester(profileMapper.toResponse(bloodRequisition.getRequester()));
        response.setPatientName(bloodRequisition.getPatientName());
        response.setPatientAge(bloodRequisition.getPatientAge());
        response.setPatientGender(bloodRequisition.getPatientGender() != null ? bloodRequisition.getPatientGender().toResponse() : null);
        response.setBloodGroup(bloodRequisition.getBloodGroup() != null ? bloodRequisition.getBloodGroup().toResponse() : null);
        response.setBloodComponent(bloodRequisition.getBloodComponent() != null ? bloodRequisition.getBloodComponent().toResponse() : null);
        response.setQuantityBags(bloodRequisition.getQuantityBags());
        response.setNeededByDate(bloodRequisition.getNeededByDate());
        response.setHospital(hospitalMapper.toResponse(bloodRequisition.getHospital()));
        response.setContactPerson(bloodRequisition.getContactPerson());
        response.setContactPhone(bloodRequisition.getContactPhone());
        response.setDescription(bloodRequisition.getDescription());
        response.setDistrict(districtMapper.toResponse(bloodRequisition.getDistrict()));
        response.setUpazila(upazilaMapper.toResponse(bloodRequisition.getUpazila()));
        response.setLat(bloodRequisition.getLat());
        response.setLon(bloodRequisition.getLon());
        response.setStatus(bloodRequisition.getRequisitionStatus() != null ? bloodRequisition.getRequisitionStatus().toResponse() : null);
        response.setFulfilledDate(bloodRequisition.getFulfilledDate());
        return response;
    }

    public BloodRequisition toEntity(BloodRequisitionRequest request) {
        if (request == null) {
            return null;
        }

        BloodRequisition entity = new BloodRequisition();
        entity.setRequester(toProfile(request.getRequesterId()));
        entity.setPatientName(request.getPatientName());
        entity.setPatientAge(request.getPatientAge());
        entity.setPatientGender(mapGender(request.getPatientGender()));
        entity.setBloodGroup(mapBloodGroup(request.getBloodGroup()));
        entity.setBloodComponent(mapBloodComponent(request.getBloodComponent()));
        entity.setQuantityBags(request.getQuantityBags());
        entity.setNeededByDate(request.getNeededByDate());
        entity.setHospital(toHospital(request.getHospitalId()));
        entity.setContactPerson(request.getContactPerson());
        entity.setContactPhone(request.getContactPhone());
        entity.setDescription(request.getDescription());
        entity.setDistrict(toDistrict(request.getDistrictId()));
        entity.setUpazila(toUpazila(request.getUpazilaId()));
        entity.setLat(request.getLat());
        entity.setLon(request.getLon());
        entity.setRequisitionStatus(statusToRequisitionStatus(request.getStatus()));
        entity.setFulfilledDate(request.getFulfilledDate());
        return entity;
    }

    public RequisitionStatus statusToRequisitionStatus(String status) {
        return StringUtils.hasText(status) ? RequisitionStatus.valueOf(status) : null;
    }

    public BloodGroup mapBloodGroup(String bloodGroup) {
        return StringUtils.hasText(bloodGroup) ? BloodGroup.valueOf(bloodGroup) : null;
    }

    public BloodComponent mapBloodComponent(String component) {
        return StringUtils.hasText(component) ? BloodComponent.valueOf(component) : null;
    }

    public Gender mapGender(String gender) {
        return StringUtils.hasText(gender) ? Gender.valueOf(gender) : null;
    }

    private Profile toProfile(Long requesterId) {
        if (requesterId == null) {
            return null;
        }
        Profile profile = new Profile();
        profile.setId(requesterId);
        return profile;
    }

    private Hospital toHospital(Integer hospitalId) {
        if (hospitalId == null) {
            return null;
        }
        Hospital hospital = new Hospital();
        hospital.setId(hospitalId);
        return hospital;
    }

    private District toDistrict(Integer districtId) {
        if (districtId == null) {
            return null;
        }
        District district = new District();
        district.setId(districtId);
        return district;
    }

    private Upazila toUpazila(Integer upazilaId) {
        if (upazilaId == null) {
            return null;
        }
        Upazila upazila = new Upazila();
        upazila.setId(upazilaId);
        return upazila;
    }

}