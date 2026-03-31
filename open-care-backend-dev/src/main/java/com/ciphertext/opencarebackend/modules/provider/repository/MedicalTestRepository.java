package com.ciphertext.opencarebackend.modules.provider.repository;

import com.ciphertext.opencarebackend.entity.MedicalTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface MedicalTestRepository extends JpaRepository<MedicalTest, Integer>, JpaSpecificationExecutor<MedicalTest> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query(
            nativeQuery = true,
            value = """
                update medical_test mt
                set hospital_count = coalesce((
                  select count(distinct hmt.hospital_id)
                  from hospital_medical_test hmt
                  where hmt.medical_test_id = mt.id
                    and hmt.hospital_id is not null
                ), 0)
                """
    )
    int refreshHospitalCounts();

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query(
            nativeQuery = true,
            value = """
                update medical_test mt
                set hospital_count = coalesce((
                  select count(distinct hmt.hospital_id)
                  from hospital_medical_test hmt
                  where hmt.medical_test_id = :id
                    and hmt.hospital_id is not null
                ), 0)
                where mt.id = :id
                """
    )
    int refreshHospitalCountById(@Param("id") Integer id);
}