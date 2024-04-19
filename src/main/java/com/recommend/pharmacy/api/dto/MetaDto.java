package com.recommend.pharmacy.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MetaDto {
    @JsonProperty("total_count") // Json형태의 total_count 라는 key를 현재 필드에 매핑시켜준다. (snake -> camel)
    private Integer totalCount; // 검색어에 검색된 문서의 전체 갯수
}
