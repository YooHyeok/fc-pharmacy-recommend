package com.recommend.pharmacy.domain.direction.controller

import com.recommend.pharmacy.domain.direction.dto.OutputDto
import com.recommend.pharmacy.domain.pharmacy.service.PharmacyRecommendationService
import org.spockframework.mock.IArgumentConstraint
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification

class FormControllerTest extends Specification {
    private MockMvc mockMvc
    private PharmacyRecommendationService pharmacyRecommendationService = Mock()
    private List<OutputDto> outputDtoList

    def setup() {
        // FormContoller를 MockMvc 객체로 Build
        mockMvc = MockMvcBuilders.standaloneSetup(new FormController(pharmacyRecommendationService)).build()

        // output List 데이터 초기화
        outputDtoList = new ArrayList<>()
        outputDtoList.addAll(
                OutputDto.builder()
                        .pharmacyName("pharmacy1")
                        .build(),
                OutputDto.builder()
                        .pharmacyName("pharmacy2")
                        .build()
        )
    }

    def "GET /"() {
        expect:
        // FormController의 "/" URI를 get방식으로 호출
        mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(MockMvcResultMatchers.handler().handlerType(FormController.class)) // 핸들링 된 컨트롤러 검증
                .andExpect(MockMvcResultMatchers.handler().methodName("main")) // 호출된 핸들러 매핑 메소드 이름 검증
                .andExpect(MockMvcResultMatchers.status().isOk()) // 응답코드 200인지 검증
                .andExpect(MockMvcResultMatchers.view().name("main")) // view resolver로 적용된 view 이름이 main인지 검증
                .andDo(MockMvcResultHandlers.log()) // 실행 결과를 디버깅 레벨로 출력
    }

    def "POST /search"() {
        given:
        String inputAddress = "서울 성북구 종암동"

        when:
        def resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/search")
                .param("address", inputAddress)
        )

        then:
        1 * pharmacyRecommendationService.recommendPharmacyList(argument -> {
            assert argument == inputAddress // mock객체의 argument 검증
        }) >> outputDtoList

        resultActions
        .andExpect(MockMvcResultMatchers.status().isOk()) // 응답 상태200  검증
        .andExpect(MockMvcResultMatchers.view().name("output")) // 뷰 이름 검증
        .andExpect(MockMvcResultMatchers.model().attributeExists("outputFormList")) // Model객체의 이름 검증
        .andExpect(MockMvcResultMatchers.model().attribute("outputFormList", outputDtoList)) // outputFormList 이름의 Model객체의 value object 검증
        .andDo(MockMvcResultHandlers.print()) // 실행 결과 지정한 대상으로 출력. (default는 System.out)
    }
}
