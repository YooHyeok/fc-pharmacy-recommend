package com.recommend.pharmacy.api.service;

import com.recommend.pharmacy.api.dto.KakaoApiResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
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
}
