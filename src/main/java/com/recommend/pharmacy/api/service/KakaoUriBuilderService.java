package com.recommend.pharmacy.api.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Slf4j
@Service
public class KakaoUriBuilderService {
    private static final String KAKAO_LOCAL_SEARCH_ADDRESS_URL = "https://dapi.kakao.com/v2/local/search/address.json";
    private static final String KAKAO_LOCAL_CATEGORY_SEARCH_URL = "https://dapi.kakao.com/v2/local/search/category.json";

    /**
     * 주소 기반으로 Kakao API에 사용될 URI 생성
     * @param address
     * @return
     */
    public URI buildUriByAddressSearch(String address) {
        /*UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(KAKAO_LOCAL_SEARCH_ADDRESS_URL);
        uriBuilder.queryParam("query", address);
        return uriBuilder.build().encode().toUri();*/
        URI uri = UriComponentsBuilder.fromHttpUrl(KAKAO_LOCAL_SEARCH_ADDRESS_URL)
                .queryParam("query", address)
                .build()
                .encode()
                .toUri();
        log.info("[KakaoUriBuilderService buildUriByAddressSearch] address: {}, uri: {}", address, uri);
        return uri;
    }

    /**
     * 카테고리를 이용하여 장소 검색하기 API를 호출하기 위한 URI를 생성
     * 문자열 기반 주소 정보를 위치기반 데이터인 위도 경도로 변환한다. (주소검색 api를 통해...)
     * 변환된 위도 경도 기준으로 반경 몇 km(radius) 이내 , 어떤 카테고리를 사용할지.
     * @param latitude
     * @param longitude
     * @param radius
     * @param category
     * @return
     */
    public URI buildUriByCategorySearch(double latitude, double longitude, double radius, String category) {

        double meterRadius = radius * 1000;

        /*UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(KAKAO_LOCAL_CATEGORY_SEARCH_URL);
        uriBuilder.queryParam("category_group_code", category);
        uriBuilder.queryParam("x", longitude);
        uriBuilder.queryParam("y", latitude);
        uriBuilder.queryParam("radius", meterRadius);
        uriBuilder.queryParam("sort","distance");
        URI uri = uriBuilder.build().encode().toUri();*/

        URI uri = UriComponentsBuilder.fromHttpUrl(KAKAO_LOCAL_CATEGORY_SEARCH_URL)
                .queryParam("category_group_code", category)
                .queryParam("x", longitude)
                .queryParam("y", latitude)
                .queryParam("radius", meterRadius)
                .queryParam("sort","distance")
                .build()
                .encode()
                .toUri();
        log.info("[KakaoAddressSearchService buildUriByCategorySearch] uri: {} ", uri);
        return uri;
    }

}
