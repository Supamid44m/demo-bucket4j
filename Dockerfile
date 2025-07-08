FROM eclipse-temurin:24-jdk AS builder
WORKDIR /app

# Copy source code
COPY . .

# 🔧 Fix permission
RUN chmod +x ./mvnw

# Build jar
RUN ./mvnw clean package -DskipTests

# ------------------------------
FROM eclipse-temurin:24-jre
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
