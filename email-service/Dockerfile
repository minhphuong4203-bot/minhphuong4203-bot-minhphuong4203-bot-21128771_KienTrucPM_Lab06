# Dockerfile for email-service
FROM openjdk:21-jdk-slim as build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=build /app/target/email-service-*.jar email-service.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "email-service.jar"]