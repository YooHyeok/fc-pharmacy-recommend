package com.recommend.pharmacy.domain.pharmacy.cache

import com.recommend.pharmacy.AbstractIntergaionContainerBaseTest
import com.recommend.pharmacy.domain.pharmacy.dto.PharmacyDto
import org.springframework.beans.factory.annotation.Autowired

class PharmacyRedisTemplateServiceTest extends AbstractIntergaionContainerBaseTest {
    @Autowired
    private PharmacyRedisTemplateService pharmacyRedisTemplateService

    def setup() {
        pharmacyRedisTemplateService.findAll()
        .forEach(dto -> {
            pharmacyRedisTemplateService.delete(dto.getId())
        })
    }

    def "save success"() {
        given:
        String pharmacyName = "name"
        String pharmacyAddress = "address"
        PharmacyDto dto =
        PharmacyDto.builder()
                .id(1L)
                .pharmacyName(pharmacyName)
                .pharmacyAddress(pharmacyAddress)
                .build()

        when:
        pharmacyRedisTemplateService.save(dto)
        List<PharmacyDto> result = pharmacyRedisTemplateService.findAll()

        println(result)

        then:
        result.size() == 1
        result.get(0).id == 1L
        result.get(0).pharmacyName == pharmacyName
        result.get(0).pharmacyAddress == pharmacyAddress
    }

    def "save fail"() {
        given:
        PharmacyDto dto =
                PharmacyDto.builder().build()

        when:
        pharmacyRedisTemplateService.save(dto)
        List<PharmacyDto> result = pharmacyRedisTemplateService.findAll()

        then:
        result.size() == 0
    }

    def "delete"() {
        given:
        String pharmacyName = "name"
        String pharmacyAddress = "address"
        PharmacyDto dto =
                PharmacyDto.builder()
                        .id(1L)
                        .pharmacyName(pharmacyName)
                        .pharmacyAddress(pharmacyAddress)
                        .build()
        when:
        pharmacyRedisTemplateService.save(dto)
        pharmacyRedisTemplateService.delete(dto.getId())
        def result = pharmacyRedisTemplateService.findAll()
        then:
        result.size() == 0


    }
}
