package com.recommend.pharmacy.domain.pharmacy.service;

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

    /**
     * 약국 전체 조회 - DTO변환 및 반환
     * @return
     */
    public List<PharmacyDto> searchPharmacyDtoList() {

        /* Redis */

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
