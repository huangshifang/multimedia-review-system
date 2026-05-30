# Stage 1: Build
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY backend/pom.xml .
RUN mvn dependency:go-offline -B
COPY backend/src ./src
RUN mvn clean package -DskipTests -B

# Stage 2: Run
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN mkdir -p /app/data /app/uploads
COPY --from=build /app/target/multimedia-review-1.0.0.jar app.jar
EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=prod
ENV DB_USERNAME=root
ENV DB_PASSWORD=root
ENV JWT_SECRET=change-me-in-production-use-a-strong-secret
ENV FILE_UPLOAD_DIR=/app/uploads
ENTRYPOINT ["java", "-jar", "app.jar"]
