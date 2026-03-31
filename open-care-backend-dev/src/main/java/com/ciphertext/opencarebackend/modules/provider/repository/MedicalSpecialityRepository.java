package com.ciphertext.opencarebackend.modules.provider.repository;

import com.ciphertext.opencarebackend.entity.MedicalSpeciality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Sadman
 */
@Repository
public interface MedicalSpecialityRepository extends JpaRepository<MedicalSpeciality, Integer>, JpaSpecificationExecutor<MedicalSpeciality> {
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query(
            nativeQuery = true,
            value = """
                update medical_speciality ms
                set doctor_count = coalesce((
                  select count(distinct t.doctor_id)
                  from (
                    select dw.medical_speciality_id, dw.doctor_id
                    from doctor_workplace dw
                    where dw.medical_speciality_id is not null
                      and dw.doctor_id is not null
                    union all
                    select dd.medical_speciality_id, dd.doctor_id
                    from doctor_degree dd
                    where dd.medical_speciality_id is not null
                      and dd.doctor_id is not null
                  ) t
                  where t.medical_speciality_id = ms.id
                ), 0)
                """
    )
    int refreshDoctorCounts();

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query(
            nativeQuery = true,
            value = """

                    update medical_speciality ms
                set doctor_count = coalesce((
                  select count(distinct t.doctor_id)
                  from (
                    select dw.medical_speciality_id, dw.doctor_id
                    from doctor_workplace dw
                    where dw.medical_speciality_id = :id
                      and dw.doctor_id is not null
                    union all
                    select dd.medical_speciality_id, dd.doctor_id
                    from doctor_degree dd
                    where dd.medical_speciality_id = :id
                      and dd.doctor_id is not null
                  ) t
                ), 0)
                where ms.id = :id
                """
    )
    int refreshDoctorCountById(@Param("id") Integer id);
}