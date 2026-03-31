package com.ciphertext.opencarebackend.modules.auth.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Configuration
@EnableConfigurationProperties(ModuleJwtProperties.class)
public class ModuleJwtConfig {

    @Bean
    @Qualifier("moduleJwtSecretKey")
    public SecretKey moduleJwtSecretKey(ModuleJwtProperties moduleJwtProperties) {
        byte[] decodedKey = Base64.getDecoder().decode(moduleJwtProperties.secret());
        return new SecretKeySpec(decodedKey, "HmacSHA256");
    }

    @Bean
    @Qualifier("moduleJwtEncoder")
    public JwtEncoder moduleJwtEncoder(@Qualifier("moduleJwtSecretKey") SecretKey secretKey) {
        return new NimbusJwtEncoder(new ImmutableSecret<>(secretKey));
    }

    @Bean
    @Qualifier("moduleJwtDecoder")
    public JwtDecoder moduleJwtDecoder(@Qualifier("moduleJwtSecretKey") SecretKey secretKey) {
        return NimbusJwtDecoder.withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }
}