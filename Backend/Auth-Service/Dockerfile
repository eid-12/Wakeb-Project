# المرحلة الأولى: بناء التطبيق باستخدام Maven
FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# المرحلة الثانية: تشغيل التطبيق (استخدام نسخة Eclipse Temurin بدلاً من Alpine)
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]
