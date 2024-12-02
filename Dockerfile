# 1. 빌드 스테이지 (멀티스테이지 빌드)
FROM ubuntu:24.04 AS builder

# 2. 필요한 패키지 설치
RUN apt-get update && apt-get install -y \
    wget \
    openjdk-17-jdk \
    unzip \
    && wget https://services.gradle.org/distributions/gradle-8.4-bin.zip -P /tmp \
    && unzip /tmp/gradle-8.4-bin.zip -d /opt \
    && ln -s /opt/gradle-8.4/bin/gradle /usr/bin/gradle \
    && apt-get clean

# 3. 작업 디렉토리 설정
WORKDIR /app

# 4. Gradle 설정 파일 복사 및 의존성 다운로드
COPY build.gradle settings.gradle /app/
RUN gradle dependencies --no-daemon

# 5. 프로젝트 소스 복사 및 빌드
COPY . /app/
RUN gradle bootJar --no-daemon

# 6. 실행 이미지 생성
FROM ubuntu:24.04

# 7. 필요한 패키지 설치 (JDK)
RUN apt-get update && apt-get install -y \
    openjdk-17-jdk \
    && apt-get clean

# 8. JAR 파일 복사
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar

# 9. Spring 프로파일 설정 및 실행
ENV SPRING_PROFILES_ACTIVE=prod
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
