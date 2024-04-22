package com.recommend.pharmacy.domain.pharmacy.entity;

import com.recommend.pharmacy.BaseEntity;
import lombok.*;

import javax.persistence.*;

@Entity(name = "pharmacy")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pharmacy extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String pharmacyName;
    private String pharmacyAddress;
    private double latitude;
    private double longitude;

    /**
     * 주소 변경 메소드 (Dirty Checking)
     */
    public void changePharmacyAddress(String address) {
        this.pharmacyAddress = address;
    }
}
