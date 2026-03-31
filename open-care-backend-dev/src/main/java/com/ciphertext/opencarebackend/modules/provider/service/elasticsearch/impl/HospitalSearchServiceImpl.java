package com.ciphertext.opencarebackend.modules.provider.service.elasticsearch.impl;
import com.ciphertext.opencarebackend.modules.provider.dto.elasticsearch.HospitalDocument;
import com.ciphertext.opencarebackend.modules.provider.dto.elasticsearch.filter.HospitalSearchFilter;
import com.ciphertext.opencarebackend.entity.Hospital;
import com.ciphertext.opencarebackend.mapper.elasticsearch.HospitalDocumentMapper;
import com.ciphertext.opencarebackend.modules.provider.repository.HospitalRepository;
import com.ciphertext.opencarebackend.modules.provider.repository.elasticsearch.HospitalElasticsearchRepository;
import com.ciphertext.opencarebackend.modules.provider.service.elasticsearch.HospitalSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@ConditionalOnProperty(prefix = "app.search", name = "enabled", havingValue = "true", matchIfMissing = true)
@Slf4j
@RequiredArgsConstructor
public class HospitalSearchServiceImpl implements HospitalSearchService {

    private final HospitalRepository hospitalRepository;
    private final HospitalElasticsearchRepository hospitalElasticsearchRepository;
    private final HospitalDocumentMapper hospitalDocumentMapper;
    private final ElasticsearchOperations elasticsearchOperations;

    @Async("taskExecutor")
    @Override
    public void indexHospital(Integer id) {
        try {
            Hospital hospital = hospitalRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Hospital not found with id: " + id));
            HospitalDocument hospitalDocument = hospitalDocumentMapper.toDocument(hospital);
            hospitalElasticsearchRepository.save(hospitalDocument);
            log.debug("Indexed hospital {}", id);
        } catch (Exception ex) {
            log.error("Failed to index hospital {}", id, ex);
        }
    }

    @Async("taskExecutor")
    @Transactional(readOnly = true)
    @Override
    public void indexAllHospitals() {
        final int batchSize = 500; // tune as needed
        int page = 0;

        try {
            while (true) {
                org.springframework.data.domain.Page<Hospital> result =
                        hospitalRepository.findAll(org.springframework.data.domain.PageRequest.of(page, batchSize));

                if (!result.hasContent()) {
                    break;
                }

                List<HospitalDocument> docs = result.getContent().stream()
                        .map(hospitalDocumentMapper::toDocument)
                        .collect(Collectors.toList());

                hospitalElasticsearchRepository.saveAll(docs);
                log.debug("Indexed hospitals page {} ({} items)", page, docs.size());

                if (!result.hasNext()) {
                    break;
                }
                page++;
            }
        } catch (Exception ex) {
            log.error("Failed to index all hospitals", ex);
        }
    }

    @Override
    public void deleteHospitalIndex(Integer id) {
        hospitalElasticsearchRepository.deleteById(id);
    }

    @Override
    public Page<HospitalDocument> searchHospitals(HospitalSearchFilter filter, Pageable pageable) {
        Criteria criteria = new Criteria();

        if (filter.getSearchTerm() != null && !filter.getSearchTerm().isEmpty()) {
            String term = filter.getSearchTerm().trim();

            // Build OR block by chaining single-argument or(...) calls
            Criteria text = new Criteria()
                    .or(new Criteria("name").matches(term))
                    .or(new Criteria("bnName").matches(term));

            criteria = criteria.and(text);
        }

        if (filter.getDistrictId() != null) {
            criteria = criteria.and(new Criteria("districtId").is(filter.getDistrictId()));
        }

        if (filter.getUpazilaId() != null) {
            criteria = criteria.and(new Criteria("upazilaId").is(filter.getUpazilaId()));
        }

        if (filter.getHospitalType() != null) {
            criteria = criteria.and(new Criteria("hospitalType").is(filter.getHospitalType()));
        }

        CriteriaQuery query = new CriteriaQuery(criteria, pageable);

        SearchHits<HospitalDocument> searchHits = elasticsearchOperations.search(
                query, HospitalDocument.class, IndexCoordinates.of("hospitals"));

        List<HospitalDocument> hospitals = searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());

        return PageableExecutionUtils.getPage(
                hospitals,
                pageable,
                searchHits::getTotalHits);
    }

    @Override
    public Page<HospitalDocument> findNearbyHospitals(GeoPoint location, Double distance, Pageable pageable) {
        Criteria criteria = new Criteria("location")
                .within(location, String.valueOf(new Distance(distance, Metrics.KILOMETERS)));

        CriteriaQuery query = new CriteriaQuery(criteria, pageable);

        SearchHits<HospitalDocument> searchHits = elasticsearchOperations.search(
                query, HospitalDocument.class, IndexCoordinates.of("hospitals"));

        List<HospitalDocument> hospitals = searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());

        return PageableExecutionUtils.getPage(
                hospitals,
                pageable,
                searchHits::getTotalHits);
    }
}
