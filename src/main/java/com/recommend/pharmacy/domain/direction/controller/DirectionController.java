package com.recommend.pharmacy.domain.direction.controller;

import com.recommend.pharmacy.domain.direction.entity.Direction;
import com.recommend.pharmacy.domain.direction.service.DirectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.util.UriComponentsBuilder;

@Controller
@Slf4j
@RequiredArgsConstructor
public class DirectionController {

    private final DirectionService directionService;
    private static final String DIRECTION_BASE_URL = "https://map.kakao.com/link/map/";

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
        Direction resultDirection = directionService.findById(encodedId);

        String params = String.join(
                ",",
                resultDirection.getTargetPharmacyName(),
                String.valueOf(resultDirection.getTargetLatitude()),
                String.valueOf(resultDirection.getTargetLongitude())
        );

        String result = UriComponentsBuilder.fromHttpUrl(DIRECTION_BASE_URL + params)
                .toUriString();
        log.info("direction params: {}, url: {}" ,params ,result);

        return "redirect:"+result;
    }
}
