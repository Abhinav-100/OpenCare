package com.ciphertext.opencarebackend.modules.provider.repository;

import com.ciphertext.opencarebackend.entity.DoctorDegree;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for DoctorDegree entity.
 * Provides CRUD operations and custom queries for doctor degree management.
 *
 * @author Sadman
 */
@Repository
/**
 * Flow note: DoctorDegreeRepository belongs to the provider doctor/hospital module.
 * Read this with neighboring controller/service/repository files to trace request flow.
 */
public interface DoctorDegreeRepository extends JpaRepository<DoctorDegree, Long> {

    /**
     * Find all degrees for a specific doctor with pagination support.
     *
     * @param doctorId the ID of the doctor
     * @param pageable pagination information
     * @return page of doctor degrees
     */
    Page<DoctorDegree> findByDoctorId(Long doctorId, Pageable pageable);

    /**
     * Find all degrees for a specific doctor without pagination.
     *
     * @param doctorId the ID of the doctor
     * @return list of doctor degrees
     */
    List<DoctorDegree> findByDoctorId(Long doctorId);

    /**
     * Find a specific degree by ID and doctor ID for security validation.
     *
     * @param id the degree ID
     * @param doctorId the doctor ID
     * @return optional doctor degree
     */
    Optional<DoctorDegree> findByIdAndDoctorId(Long id, Long doctorId);

    /**
     * Check if a degree exists for a specific doctor.
     *
     * @param id the degree ID
     * @param doctorId the doctor ID
     * @return true if exists, false otherwise
     */
    boolean existsByIdAndDoctorId(Long id, Long doctorId);

    /**
     * Check if a duplicate degree exists for a doctor.
     *
     * @param doctorId the doctor ID
     * @param degreeId the degree ID
     * @param institutionId the institution ID
     * @return true if duplicate exists, false otherwise
     */
    boolean existsByDoctorIdAndDegreeIdAndInstitutionId(Long doctorId, Long degreeId, Long institutionId);

    /**
     * Find the latest degrees for a doctor ordered by end date.
     *
     * @param doctorId the doctor ID
     * @return list of doctor degrees ordered by end date descending
     */
    @Query("SELECT d FROM DoctorDegree d WHERE d.doctor.id = :doctorId ORDER BY d.endDate DESC")
    List<DoctorDegree> findLatestDegreesByDoctorId(@Param("doctorId") Long doctorId);

    /**
     * Count all degrees by institution ID.
     *
     * @param institutionId the institution ID
     * @return count of degrees
     */
    long countAllByInstitutionId_Id(int institutionId);

    long countByDoctorId(Long doctorId);

    /**
     * Find degrees by doctor ID and IDs for batch operations.
     *
     * @param doctorId the doctor ID
     * @param ids list of degree IDs
     * @return list of matching doctor degrees
     */
    List<DoctorDegree> findByDoctorIdAndIdIn(Long doctorId, List<Long> ids);

    /**
     * Find degrees by doctor ID, degree IDs, and institution IDs for batch duplicate checking.
     *
     * @param doctorId the doctor ID
     * @param degreeIds list of degree IDs
     * @param institutionIds list of institution IDs
     * @return list of matching doctor degrees
     */
    @Query("SELECT dd FROM DoctorDegree dd WHERE dd.doctor.id = :doctorId " +
           "AND dd.degree.id IN :degreeIds AND dd.institution.id IN :institutionIds")
    List<DoctorDegree> findByDoctorIdAndDegreeIdInAndInstitutionIdIn(
            @Param("doctorId") Long doctorId,
            @Param("degreeIds") List<Long> degreeIds,
            @Param("institutionIds") List<Long> institutionIds);
}