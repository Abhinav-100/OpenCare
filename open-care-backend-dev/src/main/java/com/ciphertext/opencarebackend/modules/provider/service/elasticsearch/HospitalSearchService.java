package com.ciphertext.opencarebackend.modules.provider.service.elasticsearch;
import com.ciphertext.opencarebackend.modules.provider.dto.elasticsearch.HospitalDocument;
import com.ciphertext.opencarebackend.modules.provider.dto.elasticsearch.filter.HospitalSearchFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

/**
 * Flow note: HospitalSearchService belongs to the provider doctor/hospital module.
 * Read this with neighboring controller/service/repository files to trace request flow.
 */
public interface HospitalSearchService {
    void indexHospital(Integer id);
    void indexAllHospitals();
    void deleteHospitalIndex(Integer id);
    Page<HospitalDocument> searchHospitals(HospitalSearchFilter filter, Pageable pageable);
    Page<HospitalDocument> findNearbyHospitals(GeoPoint location, Double distance, Pageable pageable);
}
