package com.recommend.pharmacy.domain.direction.service;

import com.recommend.pharmacy.api.dto.DocumentDto;
import com.recommend.pharmacy.domain.direction.entity.Direction;
import com.recommend.pharmacy.domain.pharmacy.dto.PharmacyDto;
import com.recommend.pharmacy.domain.pharmacy.service.PharmacyRepositoryService;
import com.recommend.pharmacy.domain.pharmacy.service.PharmacySearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectionService {

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

        )
    }
}
