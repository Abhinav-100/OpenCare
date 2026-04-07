package com.ciphertext.opencarebackend.modules.provider.service.elasticsearch.impl;
import com.ciphertext.opencarebackend.modules.provider.dto.elasticsearch.HospitalDocument;
import com.ciphertext.opencarebackend.modules.provider.dto.elasticsearch.filter.HospitalSearchFilter;
import com.ciphertext.opencarebackend.modules.provider.service.elasticsearch.HospitalSearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@ConditionalOnProperty(prefix = "app.search", name = "enabled", havingValue = "false")
/**
 * Flow note: NoopHospitalSearchService belongs to the provider doctor/hospital module.
 * Read this with neighboring controller/service/repository files to trace request flow.
 */
public class NoopHospitalSearchService implements HospitalSearchService {

    @Override
    public void indexHospital(Integer id) {
        log.debug("Search disabled: skip indexing hospital {}", id);
    }

    @Override
    public void indexAllHospitals() {
        log.debug("Search disabled: skip indexing all hospitals");
    }

    @Override
    public void deleteHospitalIndex(Integer id) {
        log.debug("Search disabled: skip deleting hospital index {}", id);
    }

    @Override
    public Page<HospitalDocument> searchHospitals(HospitalSearchFilter filter, Pageable pageable) {
        log.debug("Search disabled: returning empty hospital search results");
        return Page.empty(pageable);
    }

    @Override
    public Page<HospitalDocument> findNearbyHospitals(GeoPoint location, Double distance, Pageable pageable) {
        log.debug("Search disabled: returning empty nearby hospital results");
        return Page.empty(pageable);
    }
}
