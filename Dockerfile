FROM eclipse-temurin:21-jdk-jammy
LABEL authors="kevin"

WORKDIR /app
COPY target/*.jar app.jar

EXPOSE 6769

ENTRYPOINT ["java", "-jar", "app.jar"]