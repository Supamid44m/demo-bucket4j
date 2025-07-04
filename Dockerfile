# ✅ Stage 1: Build (ใช้ JDK 24 เพื่อ compile)
FROM eclipse-temurin:24-jdk AS builder
WORKDIR /app

# Copy source code
COPY . .

# Package jar (ต้องมี mvnw หรือใช้ maven ใน image)
RUN ./mvnw clean package -DskipTests

# ✅ Stage 2: Run (ใช้ JDK 24 แต่เฉพาะ JRE)
FROM eclipse-temurin:24-jre
WORKDIR /app

# Copy jar ที่ build มา
COPY --from=builder /app/target/*.jar app.jar

# Start Spring Boot app
ENTRYPOINT ["java", "-jar", "app.jar"]
