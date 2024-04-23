package com.recommend.pharmacy.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DocumentDto {
    @JsonProperty("address_name")
    private String addressName; // 검색어로 사용된 주소값

    @JsonProperty("y")
    private double latitude; // 위도(y)

    @JsonProperty("x")
    private double longitude; // 경도(x)

}
