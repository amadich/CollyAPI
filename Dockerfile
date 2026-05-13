# ========= BUILD STAGE =========
FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copy pom first for better cache
COPY pom.xml .

# Download dependencies
RUN mvn dependency:go-offline

# Copy source code
COPY src ./src

# Build jar
RUN mvn clean package -DskipTests


# ========= RUN STAGE =========
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy generated jar
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]