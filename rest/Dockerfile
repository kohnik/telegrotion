FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

COPY /target/rest-1.0.jar app.jar
COPY /target/classes/gcc-auth.json gcc-auth.json

# Открываем порт, на котором работает Spring Boot приложение
ENV PORT=8080
ENV GOOGLE_APPLICATION_CREDENTIALS=gcc-auth.json

EXPOSE $PORT

# Команда для запуска приложения
ENTRYPOINT ["java", "-jar", "app.jar --server.port=${PORT}"]