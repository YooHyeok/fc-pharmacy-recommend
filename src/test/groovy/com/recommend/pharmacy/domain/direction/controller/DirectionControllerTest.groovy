package com.recommend.pharmacy.domain.direction.controller

import com.recommend.pharmacy.domain.direction.service.DirectionService
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification

class DirectionControllerTest extends Specification {

    private MockMvc mockMvc
    private DirectionService directionService = Mock()

    def setup() {
        // DirectionController를 MockMvc 객체로 Build
        mockMvc = MockMvcBuilders.standaloneSetup(new DirectionController(directionService))
        .build()
    }

    def "GET /dir/{encodedId}"() {
        given:
        String encodedId = "r"
        String redirectURL = "https://map.kakao.com/link/map/pharmacy,3.11,128.11"

        when:
        directionService.findByDirectionUrlById(encodedId) >> redirectURL // Stub - redirectURL을 반환하도록 모킹
        def result = mockMvc.perform(MockMvcRequestBuilders.get("/dir/{encodedId}", encodedId))

        then:
        result.andExpect(MockMvcResultMatchers.status().is3xxRedirection()) // 리다이렉트 발생 확인
        .andExpect(MockMvcResultMatchers.redirectedUrl(redirectURL))
        .andDo(MockMvcResultHandlers.print())
    }
}
