package com.ciphertext.opencarebackend.modules.provider.repository;

import com.ciphertext.opencarebackend.entity.DoctorAssociation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author rakib
 */
@Repository
/**
 * Flow note: DoctorAssociationRepository belongs to the provider doctor/hospital module.
 * Read this with neighboring controller/service/repository files to trace request flow.
 */
public interface DoctorAssociationRepository extends JpaRepository<DoctorAssociation, Long> {
    List<DoctorAssociation> findByDoctorId(Long doctorId);
    List<DoctorAssociation> findByAssociationId(Integer associationId);
    long countByDoctorId(Long doctorId);
    boolean existsByDoctorIdAndAssociationId(Long doctorId, Integer associationId);
    java.util.Optional<DoctorAssociation> findByIdAndDoctorId(Long id, Long doctorId);

    /**
     * Find associations by doctor ID and IDs for batch operations.
     *
     * @param doctorId the doctor ID
     * @param ids list of association IDs
     * @return list of matching doctor associations
     */
    List<DoctorAssociation> findByDoctorIdAndIdIn(Long doctorId, List<Long> ids);

    /**
     * Find associations by doctor ID and association IDs for batch duplicate checking.
     *
     * @param doctorId the doctor ID
     * @param associationIds list of association IDs
     * @return list of matching doctor associations
     */
    @Query("SELECT da FROM DoctorAssociation da WHERE da.doctor.id = :doctorId " +
           "AND da.association.id IN :associationIds")
    List<DoctorAssociation> findByDoctorIdAndAssociationIdIn(
            @Param("doctorId") Long doctorId,
            @Param("associationIds") List<Integer> associationIds);
}