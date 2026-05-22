# ================================================================
# Dockerfile — multistage build
# Etapa 1: compila el jar con Maven
# Etapa 2: imagen liviana de runtime (solo JRE)
# ================================================================

# --- Etapa 1: Build ---
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Copia el wrapper y pom primero para aprovechar cache de capas.
# Si no cambia el pom.xml, Maven no re-descarga dependencias.
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN chmod +x ./mvnw
RUN ./mvnw dependency:go-offline -B

# Copia el código fuente y compila
COPY src ./src
RUN ./mvnw package -DskipTests -B

# --- Etapa 2: Runtime ---
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Usuario no-root por seguridad (nunca correr como root en prod)
RUN addgroup -S spring && adduser -S spring -G spring
USER spring

# Copia solo el jar generado
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

# SPRING_PROFILES_ACTIVE lo inyecta el entorno (docker-compose o Azure)
ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
