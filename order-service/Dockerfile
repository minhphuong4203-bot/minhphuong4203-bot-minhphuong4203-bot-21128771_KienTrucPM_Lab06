# Dockerfile for order-service
FROM openjdk:21-jdk-slim as build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=build /app/target/order-service-*.jar order-service.jar
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "order-service.jar"]
