package com.ciphertext.opencarebackend.modules.provider.repository.elasticsearch;
import com.ciphertext.opencarebackend.modules.provider.dto.elasticsearch.DoctorDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
/**
 * Flow note: DoctorElasticsearchRepository belongs to the provider doctor/hospital module.
 * Read this with neighboring controller/service/repository files to trace request flow.
 */
public interface DoctorElasticsearchRepository extends ElasticsearchRepository<DoctorDocument, Long> {
}
