FROM amazoncorretto:21
WORKDIR /app
COPY core/core-api/build/libs/core-api-*-boot.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Xmx1024m", "-jar", "app.jar"]
