# 1단계: Build stage
FROM amazoncorretto:21 AS builder
WORKDIR /app

# Gradle wrapper와 설정 파일 복사
COPY gradlew .
COPY gradle gradle
COPY settings.gradle.kts .
COPY build.gradle.kts .
COPY gradle.properties .

# 소스 코드 복사
COPY core core
COPY storage storage

# Gradle 빌드 실행
RUN chmod +x ./gradlew && \
    ./gradlew clean :core:core-api:bootJar --no-daemon && \
    rm -rf /root/.gradle /root/.kotlin /root/.m2 /app/.gradle

# 2단계: Runtime stage
FROM amazoncorretto:21
WORKDIR /app

# Build stage에서 생성된 JAR 파일만 복사
COPY --from=builder /app/core/core-api/build/libs/*-boot.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-Xmx1024m", "-jar", "app.jar"]
