package com.recommend.pharmacy.api.service

import com.recommend.pharmacy.AbstractIntergaionContainerBaseTest
import com.recommend.pharmacy.api.dto.DocumentDto
import com.recommend.pharmacy.api.dto.KakaoApiResponseDto
import com.recommend.pharmacy.api.dto.MetaDto
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.client.RestTemplate
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Specification

class KakaoAddressSearchServiceRetryTest extends AbstractIntergaionContainerBaseTest {

    @Autowired
    private KakaoAddressSearchService kakaoAddressSearchService

    /**
     * Spock에서 사용
     * Mockito의 MockBean과 같이 Spring Container 내에 있는 빈을 모킹한다.
     * KakaoUriBuilderService의 buildUriAddressSearch는 실제 카카오 API를 호출하기 위한 URI를 반환해준다.
     * 테스트에서는 실제 카카오 API를 호출하는것이 아니라 로컬호스트에 띄운 MockWebServer를 호출한다.
     * 따라서 KakaoUriBuilderService를 모킹하여 URI자체를 로컬호스트의 Mockserver에 응답해주도록 모킹한다.
     * 즉, kakaoAddressSearchService.requestAddressSearch(inputAddress)를 호출할때
     * 내부적으로 kakaoUriBuilderService의 buildUriByAddressSearch가 호출되는데, 이때 반환하는 값을
     * MockWebServer에 의해 통제하기 위해 Spring이 관리할 수 있도록 제어하는것이다.
     *
     * KakaoAddressSearchService의 requestAddressSearch()를 호출하면
     * 해당 메소드 내부에서 KakaoUriBuilderService의 buildUriByAddressSearch가가 호출된다.
     *
     * 이때 KakaoAddressSearchService는 @Autowired에 의해 Spring Container에서 관리되는 빈이므로,
     * KakaoAddressSearchService의 requestAddressSearch() 내부에서 사용하는 KakaoUriBuilderService의 객체도
     * Spring 컨테이너에 의해 관리되어야 하는게 맞다.
     *
     * 만약 일반적인 Spock의 Mock() 방식을 통해 KakaoAddressSearchService와 KakaoUriBuilderService를 모킹한다면
     * KakaoAddressSearchService의 requestAddressSearch() 내부에서 함께 의존적으로 사용되는 RestTemplate에 대한 모킹과
     * exchance().getBody()에 대한 매개변수 초기화도 모두 이곳에서 따로 설정 해야한다.
     */
    @SpringBean
    private KakaoUriBuilderService kakaoUriBuilderService = Mock()

    /**
     * MockWebServer
     * 예를들어 서버에서 직접 실패 처리를 응답값으로 보내는 등 테스트를 해야할 경우
     * 외부 서버 제어를 하기 까다롭기 때문에 웹서버를 모킹하여 응답값등을 조작할 때 사용한다.
     */
    private MockWebServer mockWebServer
    private ObjectMapper mapper = new ObjectMapper()

    private String inputAddress = "서울 성북구 종암로 10길"

    def setup() {
        mockWebServer = new MockWebServer()
        mockWebServer.start()
        println mockWebServer.port
        println mockWebServer.url("/")
    }

    def cleanup() {
        mockWebServer.shutdown()
    }

    /**
     * Retry 테스트 - 2회 시도하여 1회만 성공
     * MockWebServer를 사용하여 uri값을 기대함.
     * @return
     */
    def "requestAddressSearch retry success"() {
        given:
        def metaDto = new MetaDto(1)
        def documentDto = DocumentDto.builder()
                .addressName(inputAddress)
                .build()
        def expectedResponse = new KakaoApiResponseDto(metaDto, Arrays.asList(documentDto))
        def uri = mockWebServer.url("/").uri()

        when:
        mockWebServer.enqueue(new MockResponse().setResponseCode(504))
        mockWebServer.enqueue(
                new MockResponse().setResponseCode(200)
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(mapper.writeValueAsString(expectedResponse))
        )

        def kakaoApiResult = kakaoAddressSearchService.requestAddressSearch(inputAddress)

        then:
        2 * kakaoUriBuilderService.buildUriByAddressSearch(inputAddress) >> uri // uri값을 2회 기대함. (retry maxAttempts 즉 최대 재시도 2회)

        kakaoApiResult.getDocumentList().size() == 1
        kakaoApiResult.getMetaDto().totalCount == 1
        kakaoApiResult.getDocumentList().get(0).getAddressName() == inputAddress
    }

    /**
     * Retry 테스트 - 2회 시도하여 모두 실패
     * MockWebServer를 사용하여 uri값을 기대함.
     * @return
     */
    def "requestAddressSearch retry fail "() {
        given:
        def uri = mockWebServer.url("/").uri()

        when:
        mockWebServer.enqueue(new MockResponse().setResponseCode(504))
        mockWebServer.enqueue(new MockResponse().setResponseCode(504))

        def result = kakaoAddressSearchService.requestAddressSearch(inputAddress)

        then:
        2 * kakaoUriBuilderService.buildUriByAddressSearch(inputAddress) >> uri
        result == null

    }
}
