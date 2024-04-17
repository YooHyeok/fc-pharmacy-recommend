
# *Docker*

### 1. login
terminal에서 docker hub에서 가입시 입력했던 계정과 비밀번호를 입력하여 로그인한다.
```bash
docker login
```
**Username:** `가입시 입력했던 계정`  
**Password:** `가입시 입력했던 패스워드`

### 2. jar파일 이름 설정
`app.jar` 라는 이름으로 빌드성공시 생성되는 jar파일 이름을 설정한다.
- build.gradle
    ```json
    bootJar {
      archiveFileName = 'app.jar'
    }
    ```
  
### 3. Build 명령
```bash
./gradlew build
```

### 4. Dockerfile
```dockerfile
FROM openjdk:11
# openjdk 11을 사용
ARG JAR_FILE=build/libs/app.jar
COPY ${JAR_FILE} ./app.jar
# COPY 명령을 통해 JAR_FILE 이라는 ARG 변수에 저장된 경로에 있는 jar를 Docker Container 내부로 카피한다.
ENV TZ=Asia/Seoul
ENTRYPOINT ["java", "-jar", "./app.jar"]
# Docker Container 내부에서 실행시킬 명령어를 미리 등록 (build된 jar파일을 서버에 구동)
```

### 5. Docker Image Build (생성)
```bash
docker build -t {docker hub id}/application-pharmacy-test .
```
docker hub id를 명령어에 함께 작성해야 하는 이유는 docker hub에 push할 때 아이디가 없으면    
어떤 repository로 push해야 될지 찾을수 없게 된다.  
참고로 docker hub id는 로그인시 입력하는 계정이 아닌 프로필에 뜨는 닉네임이다.  

### 6. Docker Container 생성 및 실행 명령

Docker run 명령을 통해 컨테이너 생성 및 시작
```bash
docker run {docker hub id}application-pharmacy-test -p 8080:8080
```
Docker Container는 독립된 공간에서 실행되기 때문에 호스트와 도커 컨테이너간에 포트 포워딩이 필요하다.
(호스트는 OS를 말함)
즉, host로 request온 요청을 docker container로 forwarding 해줘야 한다.

### 7. Docker Container 실행 여부 확인
```bash
docker ps
```

### 8. 실제 Container 내부 접속 명령
Shell이나 Bash등 터미널 환경으로 접근 가능하다.  
container id에는 docker ps로 확인이 가능하다.  
```bash
docker exec -it {container id} bash
```
`root@{container id}:/#` 와 같은 문구가 뜬다면 터미널 환경으로 container에 접속된것이다.
```bash
ls
```
위 명령을 통해 해당 컨테이너 내부에 카피한 app.jar파일 등 컨테이너 내부의 목록을 확인할 수 있게 된다.

# *Docker Compose*
멀티 컨테이너 도커 어플리케이션을 정의하고 실행하는 도구이다.  
주로 싱글 컨테이너로는 사용하지 않고 보통 멀티컨테이너로 배포하는데, 예를들어 Application, Database, 
Redis, Nginx 등 각각을 독립적인 컨테이너로 만들어 다중 컨테이너 환경으로 구성한다.  
이렇게 구성한 다중 컨테이너 라이프사이클(네트워크 연결, 실행 순서 자동관리)을 도커 컴포즈가 관리해준다.  
docker-compose.yml파일을 작성하여 1회 실행하는 것으로 설정된 모든 컨테이너를 실행한다.  
(여러 설정값, 실행순서)

## docker-compose.yml의 profile별 분리
로컬에서 개발할때 SpringBoot Application, Database, Redis 모두 다 컨테이너로 띄워 개발한다면  
디버깅하기가 까다롭기 때문에 로컬 환경에서는 Database와 Redis만 컨테이너로 만들어 띄워두고,  
SpringBoot Application은 IntelliJ를 통해 편하게 디버깅하며 테스트할 수 있도록 환경을 세팅한다.  
(물론 배포시에는 Application, Database Redis 모두 다중 컨테이너로 구성하여 배포한다.) 

### volumes 옵션
호스트 디렉토리:컨테이너 디렉토리 형태로 설정하여 각각의 디렉토리를 연결해준다. 
컨테이너의 디렉토리가 호스트 디렉토리를 참조하여 호스트 디렉토리에있는 파일을 컨테이너의 디렉토리로 설정한는 파일에서
참조하여 사용하게된다.
예를들어 *./database/config*`:`*etc/mysql/conf.d* 와 같이 설정했다면  
컨테이너의 *etc/mysql/conf.d*파일이 호스트의(프로젝트) *database/config* 디렉토리에 존재하는 파일을 참조하게 된다.  

### 구성 파일
- ./redis/`Dockerfile`
- ./database/`Dockerfile`
- ./database/config/`mariadb.cnf`
- ./`docker-compose-local.yml`
- ./`.env`

## Docker-Compose 실행
먼저 Docker Desktop을 실행시킨다.
```bash
docker-compose -f docker-compose-local.yml up
```
`-f` 옵션을 통해 어떤 docker-compose파일을 실행시킬지 명시한다.

## .env 환경변수 IDE 설정
edit - Edit Configuration Settings - Environment Variables에 .env에 설정한 값을 Key:Value;Key:Value 형태로 지정해줘야한다.

# *테스트코드의 중요성*

- #### 테스트 코드는 기능에 대한 불확실성을 감소시킬 수 있으며, 개발자가 만든 기능을 안전하게 보호해준다.  
  예를들어 A라는 기능을 추가로 개발하여 오픈했더니 기존에 잘 사용하던 B라는 기능에 문제가 발생하였다.  
  (원인을 파악해 보면 A, B 두 기능의 의존(디펜던시)가 존재.) 
  만약 B기능의 테스트코드가 꼼꼼히 작성되어 있었다면, 배포 전 발견할 수 있었을 것이다.  
  의존 관계로 인해 A라는 기능을 추가함으로써 B기능의 테스트 케이스가 깨졌을 수 있기 때문이다.  
  이런 상황에서는 개발후 배포 전 발견했을 가능성이 있기 때문에 테스트 코드가 굉장히 중요하다.  

- #### 테스트 코드 작성은 개발 단계 초기에 문제 발견에 도움을 주기 때문에, 개발 시간을 단축한다.
  `충분한 테스트 코드 없이 빠르게 배포` → `배포 후 문제 발생` → `원인 파악, 다시 테스트 및 코드 수정` → `야근`  
  테스트 코드를 작성하는것이 오히려 시간이 아깝다는 생각이 들어, 충분한 테스트 코드 없이 빠르게 개발해나가며 배포를 했다고 가정한다.  
  배포 후 만약 문제가 발생하게 되면 그 때에는 위의 과정을 다시 밟아가야 하기 때문에 오히려 개발 시간이 많이 걸릴 수 있다.  
  
  

## Spock
Groovy 언어를 이용하여 테스트 코드를 작성할 수 있는 프레임워크로 Junit과 비교하여 코드를 더 간결하게 작성할 수 있다.

### Groovy
Gradle에서 사용하는 동적 타입 프로그래밍 언어로 JVM위에서 동작하며 자바 문법과 유사하다.
테스트 메소드 이름을 문자열로 작성할 수 있으며, given when then 코드 블록을 명확히 구분할 수 있다.  
(Junit의 경우 주석으로 블록을 구분하였고, 메소드 네이밍 또한 어노테이션 등 제약사항이 존재)


### Spock 테스트코드 작성 순서
1. 테스트 클래스는 Groovy로 클래스를 생성하고 Specification 클래스를 상속받는다.
2. feature(테스트 메서드)는 `def` 키워드를 이용하여 함수로 선언하며, 하나 이상의 블록이 존재해야 한다.  
3. **given**블록: 테스트에 필요한 값을 준비한다. 
4. **when**블록: 테스트할 코드를 실행한다.
5. **then**블록: when과 함께 사용하며 예외 및 결과값을 검증한다. 
6. **expect**블록: then과 같으며 when을 필요로 하지 않기 때문에 간단한 테스트 및 where와 같이 사용된다. 
7. **where**블록: 데이터가 다르고 로직이 동일한 경우 동일한 테스트에 대한 중복 코드 제거가 가능하다.

### Where 블록 예제
where은 | 기호로 구분한 Data table로 생성이 가능하며, 파라미터, 결과값을 보기 좋게 구분이 가능하다.
```groovy

def "whereTestExample"() {
  expect:
  result == addressConverterService.convertAddressToGeospatialData(address).isPresent()
  where:
  address                     | result
  "서울 특별시 성북구 종암동"     | true
  "서울 성북구 종암동 91"        | true
  "서울 성북구 종암동"           | true
  "서울 성북구 종암동 잘못된 주소" | false
  "광진구 구의동 251-45"        | true
  "광진구 구의동 251-455555"    | false
  ""                          | false
}
```


- Dependecy 추가
  ```json
  plugins {
    /* 생략 */
    id 'groovy' // 추가
  }
  dependencies {
   /* 생략 */ 
    
  //spock
  testImplementation('org.spockframework:spock-core:2.1-groovy-3.0')
  testImplementation('org.spockframework:spock-spring:2.1-groovy-3.0')
  
  // 런타임에 클래스 기반 spock mock을 만들기 위해 필요
  testImplementation('net.bytebuddy:byte-buddy:1.12.10')
  }
  ```

`test` 디렉토리 마우스 우클릭 - `Directory` - `groovy` 추가

# *TestContainers*

### 사용 이유
- JPA이용 CRUD 테스트코드를 작성할때
