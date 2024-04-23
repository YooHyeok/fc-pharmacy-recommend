package com.recommend.pharmacy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.support.RetryTemplate;

@EnableRetry // Spring Retry 활성화
@Configuration
public class RetryConfig {

    /**
     * RetryTemplate 사용시 아래와 같이 빈으로 등록해준다.
     * @return
     */
//    @Bean
//    public RetryTemplate retryTemplate() {
//        return new RetryTemplate();
//    }
}
