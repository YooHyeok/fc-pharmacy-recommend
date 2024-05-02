package com.recommend.pharmacy.domain.direction.service;

import com.recommend.pharmacy.api.dto.DocumentDto;
import com.recommend.pharmacy.api.service.KakaoCategorySearchService;
import com.recommend.pharmacy.domain.direction.entity.Direction;
import com.recommend.pharmacy.domain.direction.repository.DirectionRepository;
import com.recommend.pharmacy.domain.pharmacy.dto.PharmacyDto;
import com.recommend.pharmacy.domain.pharmacy.service.PharmacySearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * - 카카오 주소API를 통해 문자열 기반 주소 정보중 가장 첫 데이터의 위치기반 데이터인 위,경도로 변환
 *
 * [buildDirectionList]
 * 	- 거리계산 알고리즘 적용으로 목록 추출 - `pharmacySearchService.searchPharmacyDtoList()`
 * 		- DB에 저장된 약국 데이터 수집
 * 		- 고객 약국사이 거리 계산 Hibersine folmular 적용 및 정렬
 * [buildDirectionListBytCategoryApi]
 * 	- 카테고리 검색 API 활용으로 목록 추출 - `kakaoCategorySearchService.requestPharmacyCategorySearch()`
 * 		- 위,경도 데이터를 기준 파라미터로 카카오 카테고리 장소검색 API를 통한 약국데이터 수집
 * 		- 고객 약국사이 거리 정보 distance API로 부터 반환 및 정렬
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DirectionService {

    private static final int MAX_SEARCH_COUNT = 3;// 약국 최대 검색 갯수
    private static final double RADIUS_KM = 10.0;// 반경 10 KM
    private static final String DIRECTION_BASE_URL = "https://map.kakao.com/link/map/";
    private final PharmacySearchService pharmacySearchService;
    private final DirectionRepository directionRepository;
    private final KakaoCategorySearchService kakaoCategorySearchService;
    private final Base62Service base62Service;

    public String findByDirectionUrlById(String encodedId) {

        Long decodedId = base62Service.decodeDirectionId(encodedId);
        Direction resultDirection = directionRepository.findById(decodedId).orElse(null);
        String params = String.join(
                ",",
                resultDirection.getTargetPharmacyName(),
                String.valueOf(resultDirection.getTargetLatitude()),
                String.valueOf(resultDirection.getTargetLongitude())
        );

        String result = UriComponentsBuilder.fromHttpUrl(DIRECTION_BASE_URL + params)
                .toUriString();
        log.info("direction params: {}, url: {}" ,params ,result);
        return result;
    }

    /**
     * 최대 3개의 약국데이터 거리계산 목록 벌크 저장
     */
    @Transactional
    public List<Direction> saveAll(List<Direction> directionList) {
        if(CollectionUtils.isEmpty(directionList)) return Collections.emptyList(); // 목록이 비어있으면 저장처리하지 않고 빈 컬렉션 반환
        return directionRepository.saveAll(directionList);
    }

    /**
     * 최대 3개의 약국 데이터
     * Kakao API를 통해 고객이 입력한 문자열 기반 주소 정보를 통해 위치기반 데이터 위/경도로 반환하여
     * DB에 저장된 데이터를 수집한뒤, 거리계산 알고리즘을 적용하여 고객과 약국 사이의 거리를 계산하고 정렬한다.
     * 해당 반환값은 List형태로 담겨있다.
     */
    public List<Direction> buildDirectionList(DocumentDto documentDto) {

        /* 데이터가 null인 경우에 대한 유효성 검증 (입력한 주소가 존재하지 않을 경우) */
        if(Objects.isNull(documentDto)) return Collections.emptyList();

        // 약국데이터 조회
        List<PharmacyDto> pharmacyDtos = pharmacySearchService.searchPharmacyDtoList();

        //거리 계산 알고리즘을 이용하여, 고객과 약국 사이의 거리를 계산하고 sort
        return pharmacyDtos.stream().map(pharmacyDto ->
                        Direction.builder()
                                /* 고객이 입력한 정보(고객위치) */
                                .inputAddress(documentDto.getAddressName())
                                .inputLatitude(documentDto.getLatitude())
                                .inputLongitude(documentDto.getLongitude())
                                /* 약국의 정보 */
                                .targetPharmacyName(pharmacyDto.getPharmacyName())
                                .targetAddress(pharmacyDto.getPharmacyAddress())
                                .targetLatitude(pharmacyDto.getLatitude())
                                .targetLongitude(pharmacyDto.getLongitude())
                                /* 고객-약국간 거리 (하버사인포뮬러) */
                                .distance(
                                        calcutateDistance(documentDto.getLatitude(), documentDto.getLongitude(),
                                                pharmacyDto.getLatitude(), pharmacyDto.getLongitude()
                                        )
                                ).build()
                )
                /* 반경 10KM 이내에 있는 Direction만 필터링 */
                .filter(direction -> direction.getDistance() <= RADIUS_KM)
                /* 거리 기준 오름차순 정렬 */
                .sorted(Comparator.comparing(Direction::getDistance))
                /* 최대 3개까지만 */
                .limit(MAX_SEARCH_COUNT)
                .collect(Collectors.toList());
    }

    /**
     * Kakao 카테고리별 장소검색 Api를 활용 <br/>
     * 반경 10KM이내 가장 가까운 인근 약국 데이터 3개 목록 조회 <br/>
     * 거리정보는 알고리즘 적용을 하지 않고 API에 의해 응답으로 받는다.
     */
    public List<Direction> buildDirectionListBytCategoryApi(DocumentDto inputDocumentDto) {
        if (Objects.isNull(inputDocumentDto)) return Collections.emptyList();

        /* Kakao 카테고리별 장소검색 Api - 약국 카테고리로 약국 목록 검색 */
        List<DocumentDto> documentList =
                kakaoCategorySearchService
                        .requestPharmacyCategorySearch(inputDocumentDto.getLatitude(), inputDocumentDto.getLongitude(), RADIUS_KM)
                        .getDocumentList();
        return documentList.stream()
                .map(resultDocumentDto ->
                        Direction.builder()
                                /* 고객이 입력한 정보(고객위치) */
                                .inputAddress(inputDocumentDto.getAddressName())
                                .inputLatitude(inputDocumentDto.getLatitude())
                                .inputLongitude(inputDocumentDto.getLongitude())
                                /* 약국의 정보 */
                                .targetPharmacyName(resultDocumentDto.getPlaceName())
                                .targetAddress(resultDocumentDto.getAddressName())
                                .targetLatitude(resultDocumentDto.getLatitude())
                                .targetLongitude(resultDocumentDto.getLongitude())
                                /* 고객-약국간 거리 */
                                .distance(resultDocumentDto.getDistance() * 0.001)// km단위
                                .build()
                )
                .limit(MAX_SEARCH_COUNT)
                .collect(Collectors.toList());
    }


    /**
     * 두 위/경도 간의 거리를 구한다. <br/>
     * Haversine formula를 적용
     */
    private double calcutateDistance(double lat1, double lon1, double lat2, double lon2) {
        /* 고객의 위도경도 */
        lat1 = Math.toRadians(lat1);
        lon1 = Math.toRadians(lon1);
        /* 약국의 위도경도 */
        lat2 = Math.toRadians(lat2);
        lon2 = Math.toRadians(lon2);
        double earthRadius = 6371; //Kilometers
        return earthRadius * Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));
    }
}
