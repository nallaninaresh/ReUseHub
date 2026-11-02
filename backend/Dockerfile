# Multi-stage Dockerfile for ReUseHub Spring Boot 3 + Java 21 Backend
# Stage 1: Build JAR using Maven
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B || true

# Copy source code and build production JAR
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Minimal runtime image
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Run as dedicated unprivileged user for security
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy compiled JAR artifact from builder
COPY --from=build /app/target/*.jar app.jar

# Dynamic port assignment for Render cloud platform
ENV PORT=8080
EXPOSE ${PORT}

# Pass PORT dynamically via JVM property to ensure Spring Boot listens on Render's assigned port
ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT} -jar app.jar"]

