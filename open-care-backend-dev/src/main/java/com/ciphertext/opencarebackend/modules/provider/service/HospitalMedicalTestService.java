package com.ciphertext.opencarebackend.modules.provider.service;
import com.ciphertext.opencarebackend.modules.provider.dto.filter.MedicalTestFilter;
import com.ciphertext.opencarebackend.entity.HospitalMedicalTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Flow note: HospitalMedicalTestService belongs to the provider doctor/hospital module.
 * Read this with neighboring controller/service/repository files to trace request flow.
 */
public interface HospitalMedicalTestService {
    Page<HospitalMedicalTest> getPaginatedDataWithFilters(MedicalTestFilter medicalTestFilter, Pageable pagingSort);
    List<HospitalMedicalTest> getMedicalTestsByHospitalId(Long hospitalId);

    HospitalMedicalTest createMedicalTest(HospitalMedicalTest medicalTest);

    HospitalMedicalTest updateMedicalTest(HospitalMedicalTest medicalTest, Long id);

    void deleteMedicalTestById(Long id);

    HospitalMedicalTest getMedicalTestById(Long id);

    HospitalMedicalTest getMedicalTestByIdAndHospitalId(Long hospitalId, Long id);
}
