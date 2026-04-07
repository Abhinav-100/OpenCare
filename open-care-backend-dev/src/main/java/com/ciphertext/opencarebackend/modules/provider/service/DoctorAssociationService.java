package com.ciphertext.opencarebackend.modules.provider.service;
import com.ciphertext.opencarebackend.modules.provider.dto.request.DoctorAssociationBatchRequest;
import com.ciphertext.opencarebackend.modules.provider.dto.response.DoctorAssociationResponse;
import com.ciphertext.opencarebackend.entity.DoctorAssociation;
import com.ciphertext.opencarebackend.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author Rakib
 */
/**
 * Flow note: DoctorAssociationService belongs to the provider doctor/hospital module.
 * Read this with neighboring controller/service/repository files to trace request flow.
 */
public interface DoctorAssociationService {
    Page<DoctorAssociation> getPaginatedDataWithFilters(Pageable pageable);
    List<DoctorAssociation> getAllDoctorAssociations();
    List<DoctorAssociation> getDoctorAssociationsByDoctorId(Long doctorId);
    List<DoctorAssociation> getDoctorAssociationsByAssociationId(Integer associationId);
    DoctorAssociation getDoctorAssociationById(Long id) throws ResourceNotFoundException;
    DoctorAssociation getDoctorAssociationByIdAndDoctorId(Long doctorId, Long id) throws ResourceNotFoundException;
    DoctorAssociation createDoctorAssociation(DoctorAssociation doctorAssociation);
    DoctorAssociation updateDoctorAssociationById(Long doctorId, DoctorAssociation doctorAssociation, Long id) throws ResourceNotFoundException;
    void deleteDoctorAssociationById(Long doctorId, Long id) throws ResourceNotFoundException;

    /**
     * Batch upsert doctor associations. Creates new associations if id is null,
     * otherwise updates existing associations.
     *
     * @param doctorId the doctor ID
     * @param requests list of batch requests
     * @return list of created/updated association responses
     */
    List<DoctorAssociationResponse> batchUpsertDoctorAssociations(Long doctorId, List<DoctorAssociationBatchRequest> requests);
}
