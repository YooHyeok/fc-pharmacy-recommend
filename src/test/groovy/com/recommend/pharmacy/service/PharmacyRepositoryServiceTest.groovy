package com.recommend.pharmacy.service

import com.recommend.pharmacy.AbstractIntergaionContainerBaseTest
import com.recommend.pharmacy.domain.pharmacy.entity.Pharmacy
import com.recommend.pharmacy.domain.pharmacy.repository.PharmacyRepository
import com.recommend.pharmacy.domain.pharmacy.service.PharmacyRepositoryService
import org.springframework.beans.factory.annotation.Autowired

class PharmacyRepositoryServiceTest extends AbstractIntergaionContainerBaseTest {

    @Autowired
    private PharmacyRepositoryService pharmacyRepositoryService

    @Autowired
    private PharmacyRepository pharmacyRepository

    def setup() {
        pharmacyRepository.deleteAll()
    }

    def "PharmacyRepository update - dirty checking success"() {
        given:
        String inputAddress = "서울 특별시 성북구 종암동"
        String modifiedAddress = "서울 광진구 구의동"
        String name = "은혜 약국"

        def pharmacy = Pharmacy.builder()
                .pharmacyAddress(inputAddress)
                .pharmacyName(name)
                .build()

        when:
        def savedPharmacy = pharmacyRepository.save(pharmacy) // 저장
        pharmacyRepositoryService.updateAddress(savedPharmacy.getId(), modifiedAddress) // Dirty Checking

        def result = pharmacyRepository.findAll()

        then:
        result.get(0).getPharmacyAddress() == modifiedAddress
    }

    def "PharmacyRepository update - dirty checking failed"() {
        given:
        String inputAddress = "서울 특별시 성북구 종암동"
        String modifiedAddress = "서울 광진구 구의동"
        String name = "은혜 약국"

        def pharmacy = Pharmacy.builder()
                .pharmacyAddress(inputAddress)
                .pharmacyName(name)
                .build()

        when:
        def savedPharmacy = pharmacyRepository.save(pharmacy) // 저장
        pharmacyRepositoryService.updateAddressWithoutTransaction(savedPharmacy.getId(), modifiedAddress) // Dirty Checking

        def result = pharmacyRepository.findAll()

        then:
        result.get(0).getPharmacyAddress() == modifiedAddress
    }

    def "self invocation"() {
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
                .build();

        when:
        pharmacyRepositoryService.bar(List.of(pharmacy))

        then:
        def e = thrown(RuntimeException.class) // thrown() 메소드를 통해 RuntimeException 발생 여부 확인
        println e.message
        def result = pharmacyRepositoryService.findAll()
        result.size() == 1 // 롤백이 됬을 경우 size는 0이여야 하지만 실제로는 1임.. (트랜잭션 정상적용안됨)
    }
}
