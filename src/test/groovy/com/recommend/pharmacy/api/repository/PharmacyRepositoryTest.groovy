package com.recommend.pharmacy.api.repository

import com.recommend.pharmacy.AbstractIntergaionContainerBaseTest
import com.recommend.pharmacy.api.entity.Pharmacy
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

import java.time.LocalDateTime

@SpringBootTest // 스프링 통합테스트 환경 구축 (컨테이너로부터 빈을 주입받기위해 선언)
class PharmacyRepositoryTest extends AbstractIntergaionContainerBaseTest {

    @Autowired
    private PharmacyRepository pharmacyRepository

    def setup() {
        pharmacyRepository.deleteAll()
    }

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

    /**
     * 테스트 컨테이너의 ryuk
     * 테스트가 실행될 때 docker ps 명령으로 확인하게 되면
     * 5f2132c13e0a   mariadb:10                  "docker-entrypoint.s…"   11 seconds ago   Up 10 seconds   0.0.0.0:52148->3306/tcp   ecstatic_zhukovsky
     * 8ac381ff71c3   redis:6                     "docker-entrypoint.s…"   15 seconds ago   Up 14 seconds   0.0.0.0:52123->6379/tcp   hopeful_austin
     * a217f2cce2f2   testcontainers/ryuk:0.3.3   "/app"                   16 seconds ago   Up 15 seconds   0.0.0.0:52118->8080/tcp   testcontainers-ryuk-0f2ce279-55d2-4de5-a2dd-c958ea1c8e28
     * 위와같이 컨테이너가 확인되고 이때 알수없는 ryuk라는 컨테이너도 함께 올라온다.
     * 테스트가 종료되면 테스트 컨테이너에 의해 실행된 mariadb, redis 컨테이너가 목록에서 정리된다.
     * 이때, ryuk 컨테이너가 정리해주는 역할을 해준다.
     */
    def "PharmacyRepository saveAll"() {
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
        pharmacyRepository.saveAll(Arrays.asList(pharmacy))
        def result = pharmacyRepository.findAll()

        then:
        result.size() == 1;
    }

    /**
     * Auditing 테스트!
     */
    def "BaseTimeEntity 등록"() {
        given:
        LocalDateTime now = LocalDateTime.now()
        String address = "서울 특별시 성북구 종암동"
        String name = "은혜 약국"
        def build = Pharmacy.builder()
            .pharmacyAddress(address)
            .pharmacyName(name)
            .build()
        when:
        pharmacyRepository.save build
        def result = pharmacyRepository.findAll()
        then:
        result.get(0).getCreatedDate()isAfter(now) //매개변수로 넘겨받은 값 보다 더 최근인지 확인하는 메소드
        result.get(0).getModifieDate()isAfter(now)
    }
}
