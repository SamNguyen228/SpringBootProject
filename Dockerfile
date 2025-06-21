# Dùng image Java chính thức
FROM openjdk:17-jdk-slim

# Tạo thư mục trong container
WORKDIR /app

# Copy file JAR vào container
COPY target/*.jar ecommerce-0.0.1-SNAPSHOT.jar

# Mở cổng 8080
EXPOSE 8080

# Lệnh chạy ứng dụng
ENTRYPOINT ["java", "-jar", "ecommerce-0.0.1-SNAPSHOT.jar"]
