# ===== Build stage =====
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy toàn bộ repo
COPY . .

# Chạy Maven trong đúng thư mục có pom.xml
WORKDIR /app/example
RUN mvn clean package -DskipTests

# ===== Run stage =====
FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /app/example/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
