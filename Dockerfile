FROM eclipse-temurin:17-jre
LABEL authors="mahamatallatchimi"

WORKDIR /app

# Copier uniquement le jar (pas d’uploads en prod)
COPY build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
