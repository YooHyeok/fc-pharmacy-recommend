package com.recommend.pharmacy.domain.direction.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OutputDto {
    private String pharmacyName;    // 약국 명
    private String pharmacyAddress; // 약국 주소
    private String directionUrl;    // 길 안내 url
    private String roadViewUrl;     // 로드 뷰 url
    private String distance;        // 고객 주소와 약국 주소의 거리
}
