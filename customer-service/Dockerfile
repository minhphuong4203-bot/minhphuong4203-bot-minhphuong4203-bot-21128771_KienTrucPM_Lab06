# Dockerfile for customer-service
FROM openjdk:21-jdk-slim as build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=build /app/target/customer-service-*.jar customer-service.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "customer-service.jar"]
