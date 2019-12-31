package com.dangdang.cymbal.web.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

/**
 * For response body's format of REST request.
 *
 * @auther GeZhen
 */
@Configuration
public class JacksonConfiguration {

    @Bean
    public MappingJackson2HttpMessageConverter getMappingJackson2HttpMessageConverter() {
        return new MappingJackson2HttpMessageConverter();
    }
}
