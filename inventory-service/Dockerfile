# Dockerfile for inventory-service
FROM openjdk:21-jdk-slim as build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=build /app/target/inventory-service-*.jar inventory-service.jar
EXPOSE 8086
ENTRYPOINT ["java", "-jar", "inventory-service.jar"]