FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY pom.xml .
COPY mvnw .
COPY .mvn/ .mvn/

# Make Maven wrapper executable
RUN chmod +x ./mvnw

# Copy source code
COPY src/ src/

# Build application
RUN ./mvnw package -DskipTests

# Run application
ENTRYPOINT ["java", "-jar", "target/identity-service-0.0.1-SNAPSHOT.jar"]
