
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
