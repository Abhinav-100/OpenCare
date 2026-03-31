package com.ciphertext.opencarebackend.modules.provider.repository.elasticsearch;
import com.ciphertext.opencarebackend.modules.provider.dto.elasticsearch.HospitalDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HospitalElasticsearchRepository extends ElasticsearchRepository<HospitalDocument, Integer> {
}
