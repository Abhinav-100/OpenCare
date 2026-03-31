package com.ciphertext.opencarebackend.modules.provider.service.elasticsearch;
import com.ciphertext.opencarebackend.modules.provider.dto.elasticsearch.DoctorDocument;
import com.ciphertext.opencarebackend.modules.provider.dto.elasticsearch.filter.DoctorSearchFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DoctorSearchService {
    void indexDoctor(Long id);
    void indexAllDoctors();
    void deleteDoctorIndex(Long id);
    Page<DoctorDocument> searchDoctors(DoctorSearchFilter filter, Pageable pageable);
}
