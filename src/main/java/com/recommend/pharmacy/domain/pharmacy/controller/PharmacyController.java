package com.recommend.pharmacy.domain.pharmacy.controller;

import com.recommend.pharmacy.domain.pharmacy.cache.PharmacyRedisTemplateService;
import com.recommend.pharmacy.domain.pharmacy.dto.PharmacyDto;
import com.recommend.pharmacy.domain.pharmacy.entity.Pharmacy;
import com.recommend.pharmacy.domain.pharmacy.service.PharmacyRepositoryService;
import com.recommend.pharmacy.util.CsvUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PharmacyController {

    private final PharmacyRepositoryService pharmacyRepositoryService;
    private final PharmacyRedisTemplateService pharmacyRedisTemplateService;

    /**
     * Redis에 MariaDB에 저장된 데이터 전체 저장
     * docker ps -> ContainerID 조회
     * docker exec -it [ContainerId] redis-cli --raw
     * hgetall PHARMACY
     */
    @GetMapping("/redis/save")
    public String save() {
        /*List<PharmacyDto> pharmacyDtos = pharmacyRepositoryService.findAll()
                .stream()
                .map(pharmacy ->
                        PharmacyDto.builder()
                                .id(pharmacy.getId())
                                .pharmacyName(pharmacy.getPharmacyName())
                                .pharmacyAddress(pharmacy.getPharmacyAddress())
                                .latitude(pharmacy.getLatitude())
                                .longitude(pharmacy.getLongitude())
                                .build()
                ).collect(Collectors.toList());
        pharmacyDtos.forEach(pharmacyRedisTemplateService::save);*/
        pharmacyRepositoryService.findAll()
                .stream()
                .map(pharmacy ->
                        PharmacyDto.builder()
                                .id(pharmacy.getId())
                                .pharmacyName(pharmacy.getPharmacyName())
                                .pharmacyAddress(pharmacy.getPharmacyAddress())
                                .latitude(pharmacy.getLatitude())
                                .longitude(pharmacy.getLongitude())
                                .build()
                ).forEach(pharmacyRedisTemplateService::save);
        return "success";
    }


    @GetMapping("/csv/save")
    public String saveCsv() {
        saveCsvToDatabase(); // MariaDB 에 저장
        return "success save";
    }

    public void saveCsvToDatabase() {
        List<Pharmacy> pharmacyDtoList = CsvUtils.convertToPharmacyDtoList()
                .stream().map(pharmacyDto ->
                        Pharmacy.builder()
                                .id(pharmacyDto.getId())
                                .pharmacyName(pharmacyDto.getPharmacyName())
                                .pharmacyAddress(pharmacyDto.getPharmacyAddress())
                                .latitude(pharmacyDto.getLatitude())
                                .longitude(pharmacyDto.getLongitude())
                                .build()
                ).collect(Collectors.toList());
        pharmacyRepositoryService.saveAll(pharmacyDtoList);
    }
}
