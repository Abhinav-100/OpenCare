package com.ciphertext.opencarebackend.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
public class CoreScopeApiFilterConfig {

    @Bean
    public FilterRegistrationBean<CoreScopeApiFilter> coreScopeApiFilterRegistration(CoreScopeApiFilter filter) {
        FilterRegistrationBean<CoreScopeApiFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(filter);
        registration.addUrlPatterns("/api/*");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        registration.setName("coreScopeApiFilter");
        return registration;
    }
}