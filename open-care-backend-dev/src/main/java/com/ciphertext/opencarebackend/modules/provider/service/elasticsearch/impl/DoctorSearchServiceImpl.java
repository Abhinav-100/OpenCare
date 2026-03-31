package com.ciphertext.opencarebackend.modules.provider.service.elasticsearch.impl;
import com.ciphertext.opencarebackend.modules.provider.dto.elasticsearch.DoctorDocument;
import com.ciphertext.opencarebackend.modules.provider.dto.elasticsearch.filter.DoctorSearchFilter;
import com.ciphertext.opencarebackend.entity.Doctor;
import com.ciphertext.opencarebackend.mapper.elasticsearch.DoctorDocumentMapper;
import com.ciphertext.opencarebackend.modules.provider.repository.DoctorRepository;
import com.ciphertext.opencarebackend.modules.provider.repository.elasticsearch.DoctorElasticsearchRepository;
import com.ciphertext.opencarebackend.modules.provider.service.elasticsearch.DoctorSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
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
public class DoctorSearchServiceImpl implements DoctorSearchService {

    private final DoctorRepository doctorRepository;
    private final DoctorElasticsearchRepository doctorElasticsearchRepository;
    private final DoctorDocumentMapper doctorDocumentMapper;
    private final ElasticsearchOperations elasticsearchOperations;

    @Async("taskExecutor")
    @Override
    public void indexDoctor(Long id) {
        try {
            Doctor doctor = doctorRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Doctor not found with id: " + id));
            DoctorDocument doctorDocument = doctorDocumentMapper.toDocument(doctor);
            doctorElasticsearchRepository.save(doctorDocument);
            log.debug("Indexed doctor {}", id);
        } catch (Exception ex) {
            log.error("Failed to index doctor {}", id, ex);
        }
    }

    @Async("taskExecutor")
    @Transactional(readOnly = true)
    @Override
    public void indexAllDoctors() {
        final int batchSize = 500;
        int page = 0;

        try {
            while (true) {
                org.springframework.data.domain.Page<Doctor> result =
                        doctorRepository.findAll(org.springframework.data.domain.PageRequest.of(page, batchSize));

                if (!result.hasContent()) {
                    break;
                }

                List<DoctorDocument> docs = result.getContent().stream()
                        .map(doctorDocumentMapper::toDocument)
                        .collect(Collectors.toList());

                doctorElasticsearchRepository.saveAll(docs);
                log.debug("Indexed doctors page {} ({} items)", page, docs.size());

                if (!result.hasNext()) {
                    break;
                }
                page++;
            }
        } catch (Exception ex) {
            log.error("Failed to index all doctors", ex);
        }
    }

    @Override
    public void deleteDoctorIndex(Long id) {
        doctorElasticsearchRepository.deleteById(id);
    }

    @Override
    public Page<DoctorDocument> searchDoctors(DoctorSearchFilter filter, Pageable pageable) {
        Criteria criteria = new Criteria("isActive").is(true)
            .and(new Criteria("isVerified").is(true));

        if (filter.getSearchTerm() != null && !filter.getSearchTerm().trim().isEmpty()) {
            String term = filter.getSearchTerm().trim();

            Criteria text = new Criteria()
                    .or(new Criteria("name").matches(term))
                    .or(new Criteria("bnName").matches(term))
                    .or(new Criteria("bmdcNo").matches(term))
                    .or(new Criteria("specializations").matches(term))
                    .or(new Criteria("degrees").matches(term))
                    .or(new Criteria("description").matches(term));

            criteria = criteria.and(text);
        }

        if (filter.getDistrictId() != null) {
            criteria = criteria.and(new Criteria("districtId").is(filter.getDistrictId()));
        }

        if (filter.getUpazilaId() != null) {
            criteria = criteria.and(new Criteria("upazilaId").is(filter.getUpazilaId()));
        }

        CriteriaQuery query = new CriteriaQuery(criteria, pageable);

        SearchHits<DoctorDocument> searchHits = elasticsearchOperations.search(
                query, DoctorDocument.class, IndexCoordinates.of("doctors"));

        List<DoctorDocument> doctors = searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());

        return PageableExecutionUtils.getPage(
                doctors,
                pageable,
                searchHits::getTotalHits);
    }
}
