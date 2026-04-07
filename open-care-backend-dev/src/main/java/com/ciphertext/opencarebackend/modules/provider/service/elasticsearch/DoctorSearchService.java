package com.ciphertext.opencarebackend.modules.provider.service.elasticsearch;
import com.ciphertext.opencarebackend.modules.provider.dto.elasticsearch.DoctorDocument;
import com.ciphertext.opencarebackend.modules.provider.dto.elasticsearch.filter.DoctorSearchFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Flow note: DoctorSearchService belongs to the provider doctor/hospital module.
 * Read this with neighboring controller/service/repository files to trace request flow.
 */
public interface DoctorSearchService {
    void indexDoctor(Long id);
    void indexAllDoctors();
    void deleteDoctorIndex(Long id);
    Page<DoctorDocument> searchDoctors(DoctorSearchFilter filter, Pageable pageable);
}
