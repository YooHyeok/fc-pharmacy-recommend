package com.recommend.pharmacy.domain.pharmacy.controller;

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
