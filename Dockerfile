# ================================
# ETAPA 1: Build
# ================================
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -q

COPY src ./src
RUN mvn package -DskipTests -q

# ================================
# ETAPA 2: Runtime
# ================================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

RUN addgroup -S bienestar && adduser -S bienestar -G bienestar

COPY --from=build /app/target/*.jar app.jar

RUN mkdir -p logs && chown -R bienestar:bienestar /app

USER bienestar

ARG SPRING_DATA_MONGODB_URI
ENV SPRING_DATA_MONGODB_URI=${SPRING_DATA_MONGODB_URI}

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
