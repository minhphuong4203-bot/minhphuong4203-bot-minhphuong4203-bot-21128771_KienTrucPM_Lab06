# Dockerfile for identity-service
FROM openjdk:21-jdk-slim as build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=build /app/target/identity-service-*.jar identity-service.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "identity-service.jar"]
