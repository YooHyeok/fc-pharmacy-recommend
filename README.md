
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

### 5. Docker Image Build
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