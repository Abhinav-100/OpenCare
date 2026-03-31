package com.ciphertext.opencarebackend.modules.provider.service;
import com.ciphertext.opencarebackend.modules.provider.dto.request.DoctorWorkplaceBatchRequest;
import com.ciphertext.opencarebackend.modules.provider.dto.response.DoctorWorkplaceResponse;
import com.ciphertext.opencarebackend.modules.provider.dto.response.MedicalSpecialityResponse;
import com.ciphertext.opencarebackend.entity.DoctorWorkplace;
import com.ciphertext.opencarebackend.exception.ResourceNotFoundException;

import java.util.List;

/**
 * @author Sadman
 */
public interface DoctorWorkplaceService {
    List<DoctorWorkplace> getDoctorWorkplacesByDoctorId(Long doctorId);
    List<DoctorWorkplace> getDoctorWorkplacesByHospitalId(Integer hospitalId);
    DoctorWorkplace getDoctorWorkplaceById(Long id) throws ResourceNotFoundException;
    DoctorWorkplace getDoctorWorkplaceByIdAndDoctorId(Long doctorId, Long id) throws ResourceNotFoundException;
    DoctorWorkplace createDoctorWorkplace(DoctorWorkplace doctorWorkplace);
    DoctorWorkplace updateDoctorWorkplace(Long doctorId, DoctorWorkplace newDoctorWorkplace, Long doctorWorkplaceId);
    void deleteDoctorWorkplaceById(Long doctorWorkplaceId);
    List<MedicalSpecialityResponse> getTopMedicalSpecialities(Integer limit);

    /**
     * Batch create/update multiple doctor workplaces in a single optimized transaction.
     *
     * @param doctorId the doctor ID
     * @param requests list of workplace batch requests
     * @return list of created/updated workplace responses
     */
    List<DoctorWorkplaceResponse> batchUpsertDoctorWorkplaces(Long doctorId, List<DoctorWorkplaceBatchRequest> requests);
}
