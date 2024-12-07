# 1. 빌드 스테이지 (멀티스테이지 빌드)
FROM ubuntu:24.04 AS builder

# 2. 필요한 패키지 설치
RUN apt-get update && apt-get install -y \
    wget \
    openjdk-17-jdk \
    unzip \
    locales \  # 로케일 패키지 추가
    && locale-gen ko_KR.UTF-8 \  # UTF-8 로케일 생성
    && update-locale LANG=ko_KR.UTF-8 \  # 로케일 적용
    && wget https://services.gradle.org/distributions/gradle-8.4-bin.zip -P /tmp \
    && unzip /tmp/gradle-8.4-bin.zip -d /opt \
    && ln -s /opt/gradle-8.4/bin/gradle /usr/bin/gradle \
    && apt-get clean

# 로케일 환경 변수 설정
ENV LANG=ko_KR.UTF-8 \
    LANGUAGE=ko_KR:ko \
    LC_ALL=ko_KR.UTF-8

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
    locales \  # 로케일 패키지 추가
    && locale-gen ko_KR.UTF-8 \  # UTF-8 로케일 생성
    && update-locale LANG=ko_KR.UTF-8 \  # 로케일 적용
    && apt-get clean

# 로케일 환경 변수 설정
ENV LANG=ko_KR.UTF-8 \
    LANGUAGE=ko_KR:ko \
    LC_ALL=ko_KR.UTF-8

# 8. JAR 파일 복사 및 실행
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar

# UTF-8을 Java 실행 환경에서 강제로 적용
ENTRYPOINT ["java", "-Dfile.encoding=UTF-8", "-jar", "/app/app.jar", "--spring.profiles.active=prod"]
