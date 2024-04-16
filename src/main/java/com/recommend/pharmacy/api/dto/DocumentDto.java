package com.recommend.pharmacy.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DocumentDto {
    @JsonProperty("address_name")
    private String addressName; // 검색어로 사용된 주소값

    @JsonProperty("y")
    private String latitude; // 위도(y)

    @JsonProperty("x")
    private String longitude; // 경도(x)

}
