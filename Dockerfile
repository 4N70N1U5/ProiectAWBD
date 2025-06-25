# Multi-stage build for Spring Boot application
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Set working directory
WORKDIR /app

# Copy pom.xml first to leverage Docker cache
COPY pom.xml .

# Download dependencies (this layer will be cached if pom.xml doesn't change)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine

# Create a non-root user for security
RUN addgroup -g 1001 -S skybase && \
    adduser -S skybase -u 1001 -G skybase

# Set working directory
WORKDIR /app

# Copy the built JAR from build stage
COPY --from=build /app/target/skybase-0.0.1-SNAPSHOT.jar app.jar

# Change ownership to non-root user
RUN chown skybase:skybase app.jar

# Switch to non-root user
USER skybase

# Expose the port your app runs on
EXPOSE 8081

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

# Optional: JVM tuning for containerized environments
# ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
