package com.recommend.pharmacy.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DocumentDto {

    /* Local - 카테고리별 장소 검색 필드*/
    @JsonProperty("place_name")
    private String placeName; // 카카오 api에서 선택한 카테고리에 해당하는 장소 이름이 입력된다.
    @JsonProperty("distance")
    private double distance; // 카카오 api에 의해 위도, 경도 기준으로 가까운 약국을 알아서 찾아주기 때문에 거리계산 알고리즘 진행 필요 없음.

    /* Local - 주소 검색/카테고리별 장소 검색 공통 필드 */
    @JsonProperty("address_name")
    private String addressName; // 검색어로 사용된 주소값
    @JsonProperty("y")
    private double latitude; // 위도(y)
    @JsonProperty("x")
    private double longitude; // 경도(x)

}
