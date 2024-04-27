package com.recommend.pharmacy.domain.direction.controller;

import com.recommend.pharmacy.domain.direction.service.DirectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@Slf4j
@RequiredArgsConstructor
public class DirectionController {

    private final DirectionService directionService;

    /**
     *
     * http://localhost:8080/dir/ + base62Service.encodeDirectionId(direction.getId()))
     * outputDto에 위와같이 길안내 PK를 인코딩한 파라미터를 결합한 길안내 URL을 담아 반환 및 화면 출력
     * 출력된 길안내 url 요청 → 서버 → 엔티티 반환(서비스에서 PK 디코딩) → 짧은 URL 생성 - 리다이렉트
     * @param encodedId
     * @return
     */
    @GetMapping("/dir/{encodedId}")
    public String searchDirection(@PathVariable("encodedId") String encodedId) {
        String result = directionService.findByDirectionUrlById(encodedId);
        log.info("[DirectionController searchDirection] direction url: {}" ,result);

        return "redirect:"+result;
    }
}
