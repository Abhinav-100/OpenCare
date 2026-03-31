package com.ciphertext.opencarebackend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.util.StringUtils;

import java.time.Duration;

@Configuration
@ConditionalOnProperty(prefix = "app.search", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableElasticsearchRepositories(basePackages = "com.ciphertext.opencarebackend.repository.elasticsearch")
public class ElasticsearchConfig extends ElasticsearchConfiguration {

    @Value("${spring.elasticsearch.uris:http://localhost:9200}")
    private String elasticsearchUri;

    @Value("${spring.elasticsearch.username:}")
    private String elasticsearchUsername;

    @Value("${spring.elasticsearch.password:}")
    private String elasticsearchPassword;

    @Override
    public ClientConfiguration clientConfiguration() {
        String endpoint = elasticsearchUri.replace("http://", "").replace("https://", "");

        if (StringUtils.hasText(elasticsearchUsername)) {
            return ClientConfiguration.builder()
                    .connectedTo(endpoint)
                    .withConnectTimeout(Duration.ofSeconds(30))
                    .withSocketTimeout(Duration.ofSeconds(60))
                    .withBasicAuth(elasticsearchUsername, elasticsearchPassword)
                    .build();
        }

        return ClientConfiguration.builder()
                .connectedTo(endpoint)
                .withConnectTimeout(Duration.ofSeconds(30))
                .withSocketTimeout(Duration.ofSeconds(60))
                .build();
    }
}