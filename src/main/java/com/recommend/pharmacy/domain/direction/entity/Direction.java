package com.recommend.pharmacy.domain.direction.entity;

import com.recommend.pharmacy.BaseEntity;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity(name = "direction")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@ToString
public class Direction extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* 고객 주소 정보 */
    private String inputAddress;
    private double inputLatitude;
    private double inputLongitude;

    /* 약국 주소 정보 */
    private String targetPharmacyName;
    private String targetAddress;
    private double targetLatitude;
    private double targetLongitude;

    private double distance;

}
