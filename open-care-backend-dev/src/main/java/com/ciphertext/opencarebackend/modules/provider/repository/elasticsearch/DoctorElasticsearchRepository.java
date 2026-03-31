package com.ciphertext.opencarebackend.modules.provider.repository.elasticsearch;
import com.ciphertext.opencarebackend.modules.provider.dto.elasticsearch.DoctorDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DoctorElasticsearchRepository extends ElasticsearchRepository<DoctorDocument, Long> {
}
