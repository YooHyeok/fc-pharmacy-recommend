package com.recommend.pharmacy.api.service

import spock.lang.Specification

import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

class KakaoUriBuilderServiceTest extends Specification {

    private KakaoUriBuilderService kakaoUriBuilderService

    /**
     * 모든 feature 메소드 호출 전에 호출됨.
     * KakaoUriBuilderService 인스턴스 주입
     */
    def setup() {
        kakaoUriBuilderService = new KakaoUriBuilderService()
    }

    /**
     * URI Builder 테스트 메소드 구현
     * @return
     */
    def "buildUriByAddressSearch - 한글 파라미터의 경우 정상적으로 인코딩"() {
        given:
        String address = "서울 성북구"
        def charset = StandardCharsets.UTF_8

        when:
        def uri = kakaoUriBuilderService.buildUriByAddressSearch(address) // def: JVM에서 동작하는 groovy라는 스크립트언어를 통해 동적 타입 확인 키워드 - 명시적 타입 지정 가능
        def decodeResult = URLDecoder.decode(uri.toString(), charset) // Uri (queryParam) 디코딩

        then:
        println uri // uri 출력
        decodeResult == "https://dapi.kakao.com/v2/local/search/address.json?query=서울 성북구" // 결과값 비교
    }

}
