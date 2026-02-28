# Use a smaller base image with JDK
FROM openjdk:17-jdk-slim AS builder

# Set metadata as described above
LABEL authors="Manish"

# Set the working directory
WORKDIR /app

# Copy the built JAR file into the container
COPY build/libs/demo.jar app.jar

# Set the timezone
RUN apt-get update && \
    apt-get install -y --no-install-recommends tzdata && \
    ln -fs /usr/share/zoneinfo/Asia/Kolkata /etc/localtime && \
    dpkg-reconfigure --frontend noninteractive tzdata && \
    apt-get purge -y --auto-remove tzdata

# Install required font packages
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    fontconfig \
    libfreetype6 \
    font-liberation \
    fonts-dejavu && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Expose the application port
EXPOSE 1947

# Use a non-root user for security
RUN addgroup --system appgroup && adduser --system appuser --ingroup appgroup
USER appuser

# Set the entry point for the container
ENTRYPOINT ["java", "-jar", "app.jar"]

