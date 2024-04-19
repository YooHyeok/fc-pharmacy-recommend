package com.recommend.pharmacy.api.service

import com.recommend.pharmacy.AbstractIntergaionContainerBaseTest
import com.recommend.pharmacy.api.dto.KakaoApiResponseDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

class KakaoAddressSearchServiceTest extends AbstractIntergaionContainerBaseTest {
    @Autowired
    private KakaoAddressSearchService kakaoAddressSearchService

    def "address 파라미터 값이 null이면, requestAddressSearch 메소드는 null을 리턴한다."() {
        given:
        String address = null

        when:
        def result = kakaoAddressSearchService.requestAddressSearch address

        then:
        result == null
    }

    def "주소값이 valid하다면, requestAddressSearch 메소드는 정상적으로 document를 반환한다."() {
        given:
        def address = "서울 성북구 종암로 10길"

        when:
        def result = kakaoAddressSearchService.requestAddressSearch(address)
        println result
        then:
        result.documentList.size() > 0
        result.metaDto.totalCount > 0
        result.documentList.get(0).addressName != null


    }
}
