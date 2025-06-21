# Dùng image Java chính thức
FROM openjdk:17-jdk-slim

# Tạo thư mục trong container
WORKDIR /app

# Copy file JAR vào container
COPY target/*.jar ecommerce-0.0.1-SNAPSHOT.jar

EXPOSE 8080

# Lệnh chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]
