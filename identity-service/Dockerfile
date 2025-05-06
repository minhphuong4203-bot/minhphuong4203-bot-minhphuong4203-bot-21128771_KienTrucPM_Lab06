FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline
COPY src ./src
RUN ./mvnw package -DskipTests
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "target/identity-service-0.0.1-SNAPSHOT.jar"]
