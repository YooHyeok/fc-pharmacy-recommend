package com.recommend.pharmacy.domain.direction.service;

import com.recommend.pharmacy.api.dto.DocumentDto;
import com.recommend.pharmacy.domain.direction.entity.Direction;
import com.recommend.pharmacy.domain.direction.repository.DirectionRepository;
import com.recommend.pharmacy.domain.pharmacy.dto.PharmacyDto;
import com.recommend.pharmacy.domain.pharmacy.service.PharmacyRepositoryService;
import com.recommend.pharmacy.domain.pharmacy.service.PharmacySearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectionService {
    private final DirectionRepository directionRepository;

    private static final int MAX_SEARCH_COUNT = 3;// 약국 최대 검색 갯수
    private static final double RADIUS_KM = 10.0;// 반경 10 KM
    private final PharmacySearchService pharmacySearchService;

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
