package com.ciphertext.opencarebackend.modules.provider.repository;

import com.ciphertext.opencarebackend.entity.DoctorWorkplace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Sadman
 */
@Repository
public interface DoctorWorkplaceRepository extends JpaRepository<DoctorWorkplace, Long> {
    List<DoctorWorkplace> findByDoctorId(Long doctorId);

    List<DoctorWorkplace> findByHospital_Id(Integer hospitalId);

    @Query(value = """
              SELECT ms.id, ms.name, ms.bn_name, ms.icon, COUNT(dw.id) AS doctor_count
              FROM medical_speciality ms
              LEFT JOIN doctor_workplace dw ON ms.id = dw.medical_speciality_id AND dw.end_date IS NULL
              GROUP BY ms.id, ms.name, ms.bn_name, ms.icon
              ORDER BY doctor_count desc
              LIMIT ?1
                        """, nativeQuery = true)
    List<Object[]> findMedicalSpecialitiesWithDoctorCount(Integer limit);

    long countAllByHospital_Id(int hospitalId);

    long countByDoctorId(Long doctorId);

    /**
     * Find workplaces by doctor ID and IDs for batch operations.
     *
     * @param doctorId the doctor ID
     * @param ids list of workplace IDs
     * @return list of matching doctor workplaces
     */
    List<DoctorWorkplace> findByDoctorIdAndIdIn(Long doctorId, List<Long> ids);

    /**
     * Find workplaces by doctor ID and hospital IDs for batch duplicate checking.
     *
     * @param doctorId the doctor ID
     * @param hospitalIds list of hospital IDs
     * @return list of matching doctor workplaces
     */
    @Query("SELECT dw FROM DoctorWorkplace dw WHERE dw.doctor.id = :doctorId " +
           "AND dw.hospital.id IN :hospitalIds")
    List<DoctorWorkplace> findByDoctorIdAndHospitalIdIn(
            @Param("doctorId") Long doctorId,
            @Param("hospitalIds") List<Long> hospitalIds);

    boolean existsByDoctorIdAndHospitalId(Long doctorId, Integer hospitalId);

    java.util.Optional<DoctorWorkplace> findByIdAndDoctorId(Long id, Long doctorId);
}