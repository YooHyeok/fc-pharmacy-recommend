package com.recommend.pharmacy.api.service;

import com.recommend.pharmacy.api.dto.KakaoApiResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

import java.net.URI;


@Service
@Slf4j
@RequiredArgsConstructor
public class KakaoAddressSearchService {

    private final RestTemplate restTemplate;
    private final KakaoUriBuilderService kakaoUrilBuilderService;

    @Value("${kakao.rest.api.key}") //application.yml에 지정한 property 로드
    private String kakaoRestApiKey;


    @Retryable( // Spring Retry 활성화 어노테이션
            value = {RuntimeException.class},// Retry를 적용할 Exception 종류를 지정, default 재시도 최대 3회 딜레이 1초
            maxAttempts = 2, // 최대 2회 재시도
            backoff = @Backoff(delay = 2000) // 지연시간 2초
    )
    public KakaoApiResponseDto requestAddressSearch(String address) {
        if(ObjectUtils.isEmpty(address)) return null; // Validation검증:  주소가 null값이거나 빈값일경우

        /* uri */
        URI uri = kakaoUrilBuilderService.buildUriByAddressSearch(address);
        /* header */
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "KakaoAK " + kakaoRestApiKey);
        /* httpEntity */
        HttpEntity httpEntity = new HttpEntity<>(headers);

        // kakao api 호출
        return restTemplate.exchange(uri, HttpMethod.GET, httpEntity, KakaoApiResponseDto.class)
                .getBody(); // (url, 요청방식, HttpEntity, 반환클래스) 주입
    }

    @Recover // fallback 처리 → 재시도 처리가 모두 실패했을 경우 사용. (Retry를 활성화한 메소드의 리턴타입과 일치해야함)
    public KakaoApiResponseDto recover(RuntimeException e, String address) {
        log.error("All the retries failed. address: {}, error: {}", address, e.getMessage());
        return null;
    }
}
