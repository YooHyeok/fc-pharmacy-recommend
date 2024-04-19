package com.recommend.pharmacy.service

import com.recommend.pharmacy.AbstractIntergaionContainerBaseTest
import com.recommend.pharmacy.api.entity.Pharmacy
import com.recommend.pharmacy.api.repository.PharmacyRepository
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

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
}
