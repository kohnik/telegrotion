FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

COPY /target/backend-1.0.jar app.jar

# Открываем порт, на котором работает Spring Boot приложение
ENV PORT=8080
EXPOSE $PORT

# Команда для запуска приложения
ENTRYPOINT ["java", "-jar", "app.jar --server.port=${PORT}"]