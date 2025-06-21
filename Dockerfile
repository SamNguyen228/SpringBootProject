# ======================
# Stage 1: Build ứng dụng
# ======================
FROM maven:3.9.6-eclipse-temurin-17 AS builder

# Tạo thư mục làm việc
WORKDIR /app

# Copy toàn bộ source code vào container
COPY . .

# Build file JAR (bỏ qua test cho nhanh)
RUN mvn clean package -DskipTests

# ==========================
# Stage 2: Chạy ứng dụng JAR
# ==========================
FROM openjdk:17-jdk-slim

# Tạo thư mục làm việc
WORKDIR /app

# Copy file JAR từ stage build vào container
COPY --from=builder /app/target/*.jar ecommerce-0.0.1-SNAPSHOT.jar

# Mở cổng 8080
EXPOSE 8080

# Chạy ứng dụng
ENTRYPOINT ["java", "-jar", "ecommerce-0.0.1-SNAPSHOT.jar"]
