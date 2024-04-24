package com.recommend.pharmacy.api.service

import com.recommend.pharmacy.AbstractIntergaionContainerBaseTest
import com.recommend.pharmacy.api.dto.DocumentDto
import com.recommend.pharmacy.api.dto.KakaoApiResponseDto
import com.recommend.pharmacy.api.dto.MetaDto
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper

class KakaoAddressSearchServiceRetryTest2 extends AbstractIntergaionContainerBaseTest {

    // MockWebServer 설정
    private MockWebServer mockWebServer = new MockWebServer()
    private ObjectMapper mapper = new ObjectMapper()

    // KakaoAddressSearchService와 Mocked RestTemplate 설정
    private KakaoUriBuilderService kakaoUriBuilderService = Mock()
    private RestTemplate restTemplate = Mock()
    private KakaoAddressSearchService kakaoAddressSearchService

    private String inputAddress = "서울 성북구 종암로 10길"

    def setup() {
        mockWebServer.start()
        println mockWebServer.port
        println mockWebServer.url("/")

        // KakaoAddressSearchService 인스턴스 생성
        kakaoAddressSearchService = new KakaoAddressSearchService(restTemplate, kakaoUriBuilderService)
    }

    def cleanup() {
        mockWebServer.shutdown()
    }

    def "requestAddressSearch retry success"() {
        given:
        def metaDto = new MetaDto(1)
        def documentDto = DocumentDto.builder()
                .addressName(inputAddress)
                .build()
        def expectedResponse = new KakaoApiResponseDto(metaDto, Arrays.asList(documentDto))
        def uri = mockWebServer.url("/").uri()

        // Mocked RestTemplate 설정
        def responseEntity = new ResponseEntity(expectedResponse, HttpStatus.OK)
        restTemplate.exchange(_, _, _, _) >> responseEntity // 수정된 부분

        when:
        mockWebServer.enqueue(new MockResponse().setResponseCode(504))
        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .setBody(mapper.writeValueAsString(expectedResponse))
        )

        /**
         * 일반적인 Mocking에서는 Retry가 적용되지 않는다.
         * @Retryable 어노테이션은 Spring에서 지원하는 기능이기 때문에 해당 메소드가 Spring의 프록시로 래핑되어야 한다.
         * 다시말해 Spring에서 해당 메소드호출을 감싸고 AOP에 의해 메소드에서 발생하는 예외를 catch하여 재시도 로직을 적용하기 때문이다.
         * Mocking방식에서는 이러한 Spring의 Proxy매커니즘이 적용되지 않기 때문에 Retry가 적용되지 않는것이다.
         * 따라서 Retryable 메소드의 Retry동작을 테스트하기 위해서는 Spring Container에서 빈을 관리하고 해당 빈을 주입받아
         * Retryable 메소드를 호출하는 방식을 사용해야 한다.
         * 테스트 코드에서는 이를 위해 @MockBean이나, @SpringBean을 사용하여 Mock객체로 대체하거나, 테스트케이스에서 직접
         * Container로부터 빈을 가져와 테스트를 수행해야 한다.
         * (Spring AOP기능을 이용한 Retryable 메소드의 동작은 Mocking을 통해 재현 가능하지만 AOP를 적용하는 코드 구현이 어렵다.)
         */
        // 첫 번째 호출
        def kakaoApiResult1 = kakaoAddressSearchService.requestAddressSearch(inputAddress)
        // 두 번째 호출
        def kakaoApiResult2 = kakaoAddressSearchService.requestAddressSearch(inputAddress)

        then:
        2 * kakaoUriBuilderService.buildUriByAddressSearch(inputAddress) >> uri

        kakaoApiResult1.getDocumentList().size() == 1
        kakaoApiResult1.getMetaDto().totalCount == 1
        kakaoApiResult1.getDocumentList().get(0).getAddressName() == inputAddress
        kakaoApiResult2.getDocumentList().size() == 1
        kakaoApiResult2.getMetaDto().totalCount == 1
        kakaoApiResult2.getDocumentList().get(0).getAddressName() == inputAddress

    }

    def "requestAddressSearch retry fail"() {
        given:
        def uri = mockWebServer.url("/").uri()

        def failureResponse = new ResponseEntity(HttpStatus.GATEWAY_TIMEOUT)
        // Mocked RestTemplate 설정
        restTemplate.exchange(_, _, _, _) >> failureResponse

        when:
        mockWebServer.enqueue(new MockResponse().setResponseCode(504))
        mockWebServer.enqueue(new MockResponse().setResponseCode(504))


        def result1 = kakaoAddressSearchService.requestAddressSearch(inputAddress)
        def result2 = kakaoAddressSearchService.requestAddressSearch(inputAddress)

        then:
        2 * kakaoUriBuilderService.buildUriByAddressSearch(inputAddress) >> uri
        result1 == null
        result2 == null
    }
}
