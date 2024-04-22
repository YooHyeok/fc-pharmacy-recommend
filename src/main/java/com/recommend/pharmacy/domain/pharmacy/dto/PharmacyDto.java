package com.recommend.pharmacy.domain.pharmacy.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PharmacyDto {

    private Long id;
    private String pharmacyName;
    private String pharmacyAddress;
    private double latitude;
    private double longitude;
}
