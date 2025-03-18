# Use an official Maven image to build the application
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the project files into the container
COPY . /app

# Build the project
RUN mvn clean package

# Use a minimal JDK runtime image for the final container
FROM eclipse-temurin:21-jdk

# Set the working directory inside the container
WORKDIR /app

# Copy the built JAR file from the first stage
COPY --from=build /app/target/backend-0.0.1-SNAPSHOT.jar /app/backend.jar

# Expose the port your backend runs on
EXPOSE 8080

# Command to run the backend
CMD ["java", "-jar", "/app/backend.jar"]