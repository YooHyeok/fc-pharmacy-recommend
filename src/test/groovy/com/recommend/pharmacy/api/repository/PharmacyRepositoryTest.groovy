package com.recommend.pharmacy.api.repository

import com.recommend.pharmacy.AbstractIntergaionContainerBaseTest
import com.recommend.pharmacy.api.entity.Pharmacy
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest // 스프링 통합테스트 환경 구축 (컨테이너로부터 빈을 주입받기위해 선언)
class PharmacyRepositoryTest extends AbstractIntergaionContainerBaseTest {

    @Autowired
    private PharmacyRepository pharmacyRepository

    def "PharmacyRepository save"() {
        given:
        String address = "서울 특별시 성북구 종암동"
        String name = "은혜 약국"
        double latitude = 36.11
        double longitude = 128.11

        def pharmacy = Pharmacy.builder()
            .pharmacyAddress(address)
            .pharmacyName(name)
            .latitude(latitude)
            .longitude(longitude)
            .build()

        when:
        def result = pharmacyRepository.save(pharmacy)

        then:
        result.getPharmacyAddress() == address
        result.getPharmacyName() == name
        result.getLatitude() == latitude
        result.getLongitude() == longitude

    }
}
