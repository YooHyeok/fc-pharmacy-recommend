package com.recommend.pharmacy.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObjectMapperConfig {

    /**
     * 싱글톤 Bean 등록
     * 사용할 때 마다 인스턴스를 만드는것이 아니라,
     * 사용할 때 주입될 수 있도록 미리 싱글톤 빈 객체로 구성해둔다.
     *
     * @return
     */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
