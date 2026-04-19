# Stage 1: Build the application using a JDK
FROM eclipse-temurin:25-jdk-alpine AS build
WORKDIR /app

# Copy the maven wrapper and pom file
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Download dependencies (this step is cached if pom.xml doesn't change)
RUN chmod +x ./mvnw
RUN ./mvnw dependency:go-offline -B

# Copy the source code and build
COPY src src
RUN ./mvnw clean package -DskipTests

# Stage 2: Create the minimal runtime image
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app

# Copy the built jar from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
