package com.recommend.pharmacy.domain.pharmacy.service;

import com.recommend.pharmacy.api.dto.DocumentDto;
import com.recommend.pharmacy.api.dto.KakaoApiResponseDto;
import com.recommend.pharmacy.api.service.KakaoAddressSearchService;
import com.recommend.pharmacy.api.service.KakaoCategorySearchService;
import com.recommend.pharmacy.domain.direction.entity.Direction;
import com.recommend.pharmacy.domain.direction.service.DirectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

/**
 * 약국 추천 서비스
 * 1. 카카오 주소 API에 의해 주소를 입력하고, 정형화된 주소를 반환
 * 2. 해당 주소를 기준으로 카카오 지도 API를 통해 위도 경도 데이터를 받아옴 (첫 데이터만 사용할것임.)
 * 3. DB에 저장된 모든 약국을 기준으로 현재 검색한 위치 기준의 데이터를 함께 매핑하여 리스트로 추출
 * 4. 검색 위치와 저장되어있는 모든 약국간의 거리데이터가 담긴 리스트중 반경 10KM이내의 가장 가까운 3곳을 필터링.
 * 5. 최종 반환된 약국 데이터들을 DB에 저장함.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PharmacyRecommendationService {
    private final KakaoAddressSearchService kakaoAddressSearchService;
    private final DirectionService directionService;

    public void recommendPharmacyList(String address) {
        KakaoApiResponseDto kakaoApiResponseDto = kakaoAddressSearchService.requestAddressSearch(address); // 주소 API를 통해

        if (Objects.isNull(kakaoApiResponseDto) || CollectionUtils.isEmpty(kakaoApiResponseDto.getDocumentList())) {
            log.error("[PharmacyRecommendationService recommendPharmacyList fail] Input address: {}", address);
            return;
        }

        DocumentDto documentDto = kakaoApiResponseDto.getDocumentList().get(0);

        /* 공공기관 약국 데이터 및 거리계산 알고리즘 */
//        List<Direction> directionList = directionService.buildDirectionList(documentDto); // DB에 저장된 데이터 기준 거리계산 알고리즘 적용 - 가까운 거리 기준 최대 3개 약국 목록 반환

        /* Kakao API - 카테고리를 이용한 장소검색 api */
        List<Direction> directionList = directionService.buildDirectionListBytCategoryApi(documentDto); // Kakao 카테고리 장소검색 API 적용 - 가까운 거리 기준 최대 3개 약국 목록 반환

        directionService.saveAll(directionList); // 반환된 약국목록 DB에 저장
    }
}
