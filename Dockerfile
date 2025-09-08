FROM openjdk:17-jdk-slim
LABEL authors="mahamatallatchimi"
WORKDIR /app

COPY build/libs/*.jar app.jar
COPY uploads/ uploads/
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]