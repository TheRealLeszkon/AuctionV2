FROM eclipse-temurin:21-jdk-jammy
LABEL authors="kevin"

WORKDIR /app
COPY target/*.jar app.jar

COPY initContainer.sh /app/initContainer.sh
RUN chmod +x /app/initContainer.sh

EXPOSE 6769

ENTRYPOINT ["java", "-jar", "app.jar"]