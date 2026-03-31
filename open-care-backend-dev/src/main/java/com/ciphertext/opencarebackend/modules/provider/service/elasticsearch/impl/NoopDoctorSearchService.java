package com.ciphertext.opencarebackend.modules.provider.service.elasticsearch.impl;
import com.ciphertext.opencarebackend.modules.provider.dto.elasticsearch.DoctorDocument;
import com.ciphertext.opencarebackend.modules.provider.dto.elasticsearch.filter.DoctorSearchFilter;
import com.ciphertext.opencarebackend.modules.provider.service.elasticsearch.DoctorSearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@ConditionalOnProperty(prefix = "app.search", name = "enabled", havingValue = "false")
public class NoopDoctorSearchService implements DoctorSearchService {

    @Override
    public void indexDoctor(Long id) {
        log.debug("Search disabled: skip indexing doctor {}", id);
    }

    @Override
    public void indexAllDoctors() {
        log.debug("Search disabled: skip indexing all doctors");
    }

    @Override
    public void deleteDoctorIndex(Long id) {
        log.debug("Search disabled: skip deleting doctor index {}", id);
    }

    @Override
    public Page<DoctorDocument> searchDoctors(DoctorSearchFilter filter, Pageable pageable) {
        log.debug("Search disabled: returning empty doctor search results");
        return Page.empty(pageable);
    }
}
