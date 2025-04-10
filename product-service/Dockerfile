# Dockerfile for product-service
FROM openjdk:21-jdk-slim as build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=build /app/target/product-service-*.jar product-service.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "product-service.jar"]
