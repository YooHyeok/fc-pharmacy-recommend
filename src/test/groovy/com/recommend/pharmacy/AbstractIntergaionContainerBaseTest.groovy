package com.recommend.pharmacy

import org.springframework.boot.test.context.SpringBootTest
import org.testcontainers.containers.GenericContainer
import spock.lang.Specification

/**
 * 싱글톤 컨테이너 구성 테스트
 * 테스트가 진행될때 한번 컨테이너를 실행하고, 테스트가 모두 완료되면 컨테이너가 종료된다.
 */
@SpringBootTest // 스프링 컨테이너를 함께 띄워 여러 모듈간의 연동까지 검증하는 통합 테스트 환경
abstract class AbstractIntergaionContainerBaseTest extends Specification{

    static final GenericContainer MY_REDIS_CONTAINER

    /**
     * static 초기화자
     * Redis의 경우 TestContainer의 모듈 하위에 제공을 하고있지 않기 때문에 Docker hub의 Image를 이용하여 테스트컨테이너와 연동해야 한다.
     * 이때 GenericContainer를 활용하여 해당 이미지를 초기화 해준다.
     * 6379: Docker로부터 expose한 port
     * host port는 TestContainer가 충돌되지 않는 임의의 포트를 생성해서 매핑해준다.
     * SpringBoot입장에서는 Redis와 통신하기위해 Port를 알아야 하므로 setProperty로 시스템에 HOST 정보를 전달해준다.
     */
    static {
        /* Redis 컨테이너 초기화 */
        MY_REDIS_CONTAINER = new GenericContainer<>("redis:6") // 사용할 image와 버전 설정
                .withExposedPorts(6379)  // Docker로 부터 expose된 Port

        /* Redis 컨테이너 시작 */
        MY_REDIS_CONTAINER.start()

        /* Spring Boot에 Host & Port 정보 전달 */
        System.setProperty("spring.redis.host", MY_REDIS_CONTAINER.getHost()) // Host정보 전달
        System.setProperty("spring.redis.port", MY_REDIS_CONTAINER.getMappedPort(6379).toString()) // SpringBoot에게 6379와 랜덤하게 매핑된 port 정보 전달
    }
}
