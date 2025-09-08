FROM eclipse-temurin:17-jre
LABEL authors="mahamatallatchimi"

WORKDIR /app


# Copier le jar Maven généré
COPY target/security-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
