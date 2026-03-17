# Runtime image
FROM eclipse-temurin:21-jre-jammy

LABEL authors="Manish"

WORKDIR /app

ARG JAR_FILE=build/libs/template-api.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8080

# Use a non-root user for security
RUN addgroup --system appgroup && adduser --system appuser --ingroup appgroup
USER appuser

ENTRYPOINT ["java", "-jar", "/app/app.jar"]

