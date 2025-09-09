# -------------------------------
# Stage 1: Build Maven
# -------------------------------
FROM eclipse-temurin:17-jdk AS build

WORKDIR /app

# Copier uniquement les fichiers Maven essentiels pour le build
COPY pom.xml mvnw ./
COPY .mvn .mvn
COPY src ./src

# Build le projet
RUN ./mvnw clean package -DskipTests

# -------------------------------
# Stage 2: Runtime
# -------------------------------
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copier le jar du stage build
COPY --from=build /app/target/*.jar app.jar

# Exposer le port Spring Boot
EXPOSE 8080

# Lancer l'application
ENTRYPOINT ["java", "-jar", "app.jar"]
