package com.recommend.pharmacy.domain.direction.service

import com.recommend.pharmacy.api.dto.DocumentDto
import com.recommend.pharmacy.api.service.KakaoCategorySearchService
import com.recommend.pharmacy.domain.direction.entity.Direction
import com.recommend.pharmacy.domain.direction.repository.DirectionRepository
import com.recommend.pharmacy.domain.pharmacy.dto.PharmacyDto
import com.recommend.pharmacy.domain.pharmacy.service.PharmacySearchService
import spock.lang.Specification

class DirectionServiceTest extends Specification {

    private PharmacySearchService pharmacySearchService = Mock()
    private DirectionRepository directionRepository = Mock()
    private KakaoCategorySearchService kakaoCategorySearchService = Mock()
    private Base62Service base62Service = Mock()

    private DirectionService directionService =
            new DirectionService(pharmacySearchService, directionRepository, kakaoCategorySearchService, base62Service)

    private List<PharmacyDto> pharmacyDtoList

    def setup() {
        pharmacyDtoList = new ArrayList<>()
        pharmacyDtoList.addAll(
            PharmacyDto.builder()
                    .id(1L)
                    .pharmacyName("돌곶이온누리약국")
                    .pharmacyAddress("주소1")
                    .latitude(37.61040424)
                    .longitude(127.0569046)
                    .build(),
            PharmacyDto.builder()
                    .id(2L)
                    .pharmacyName("호수온누리약국")
                    .pharmacyAddress("주소2")
                    .latitude(37.60894036)
                    .longitude(127.029052)
                    .build()
        )
    }

    def "buildDirectionList - 결과 값이 거리 순으로 정렬 되는지 확인"() {
        given:
        def addressName = "서울 성북구 종암로10길"
        double inputLatitude = 37.5960650456809
        double inputLongitude = 127.037033003036
        def documentDto = DocumentDto.builder()
            .addressName(addressName)
            .latitude(inputLatitude)
            .longitude(inputLongitude)
            .build()

        when:
        /* Stub: Mock을 통해 미리 만들어놓은 데이터를 가져오는 행위 */
        pharmacySearchService.searchPharmacyDtoList() >> pharmacyDtoList // >> 의미: Mock 객체로부터 메소드 호출 후 pharmacyDtoList를 return해라
        def results = directionService.buildDirectionList(documentDto)

        then:
        results.size() == 2
        results.get(0).targetPharmacyName == "호수온누리약국" // 더 가까움
        results.get(1).targetPharmacyName == "돌곶이온누리약국" // 더 멈
    }

    def "buildDirectionList - 정해진 반경이 10KM 내에 검색이 되는지 확인"() {
        given:
        def addressName = "서울 성북구 종암로10길"
        double inputLatitude = 37.5960650456809
        double inputLongitude = 127.037033003036
        def documentDto = DocumentDto.builder()
                .addressName(addressName)
                .latitude(inputLatitude)
                .longitude(inputLongitude)
                .build()

        when:
        /* Stub: Mock을 통해 미리 만들어놓은 데이터를 가져오는 행위 */
        pharmacySearchService.searchPharmacyDtoList() >> pharmacyDtoList // >> 의미: Mock 객체로부터 메소드 호출 후 pharmacyDtoList를 return해라
        def results = directionService.buildDirectionList(documentDto)

        then:
        results.size() == 2
        results.get(0).targetPharmacyName == "호수온누리약국" // 더 가까움
        results.get(1).targetPharmacyName == "돌곶이온누리약국" // 더 멈
    }
}
