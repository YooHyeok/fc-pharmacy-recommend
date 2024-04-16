package com.recommend.pharmacy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;


@Configuration
public class RestTemplateConfig {

    /**
     * Spring Web에서 지원하는 RestTemplate 빈 등록
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
