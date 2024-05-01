package com.recommend.pharmacy.domain.pharmacy.service;

import com.recommend.pharmacy.domain.pharmacy.cache.PharmacyRedisTemplateService;
import com.recommend.pharmacy.domain.pharmacy.dto.PharmacyDto;
import com.recommend.pharmacy.domain.pharmacy.entity.Pharmacy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PharmacySearchService {
    private final PharmacyRepositoryService pharmacyRepositoryService;
    private final PharmacyRedisTemplateService pharmacyRedisTemplateService;

    /**
     * 약국 전체 조회 - DTO변환 및 반환
     * Redis에 약국 데이터가 비어있지 않다면 Redis로부터 데이터를 반환하고,
     * Redis에 데이터가 없으면 Maria DB에서 조회하여 반환한다.
     * 데이터가 없으면 Redis에 장애가 발생한 상태임.
     * (deserialize 도중 readValue에서 Exception발생)
     * @return
     */
    public List<PharmacyDto> searchPharmacyDtoList() {

        /* Redis */
        List<PharmacyDto> pharmacyDtos = pharmacyRedisTemplateService.findAll();
        if (!pharmacyDtos.isEmpty()) {
            log.info("redis findAll success!");
            return pharmacyDtos;
        }

        /* MariaDB - 전체 조회 후 DTO변환하여 List로 반환 */
        return pharmacyRepositoryService.findAll()
                .stream()
                .map(this::convertToPharmacyDto)
                .collect(Collectors.toList());
    }

    /**
     * Pharmacy Entity를 Pharmacy Dto로 변환
     */
    private PharmacyDto convertToPharmacyDto(Pharmacy pharmacy) {
        return PharmacyDto.builder()
                .id(pharmacy.getId())
                .pharmacyAddress(pharmacy.getPharmacyAddress())
                .pharmacyName(pharmacy.getPharmacyName())
                .latitude(pharmacy.getLatitude())
                .longitude(pharmacy.getLongitude())
                .build();
    }
}
