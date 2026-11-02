# Multi-stage Dockerfile for ReUseHub Backend (Root Context)
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Cache dependencies
COPY backend/pom.xml ./pom.xml
RUN mvn dependency:go-offline -B || true

# Copy source code and build production JAR
COPY backend/src ./src
RUN mvn clean package -DskipTests

# Stage 2: Minimal runtime image
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY --from=build /app/target/*.jar app.jar

ENV PORT=8080
EXPOSE ${PORT}

ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT} -jar app.jar"]

