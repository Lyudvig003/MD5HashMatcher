# Stage 1: Gradle Build
FROM gradle:8.6-jdk17 AS build

# Add CA certificate if needed
ARG CERTIFICATE_FILE
COPY $CERTIFICATE_FILE /usr/local/share/ca-certificates/idt.local-CA.crt
RUN update-ca-certificates
RUN keytool -importcert -trustcacerts -keystore $JAVA_HOME/lib/security/cacerts -storepass changeit -noprompt -alias artifactory -file /usr/local/share/ca-certificates/idt.local-CA.crt

# Gradle settings for repository credentials
ARG NEXUS_USER
ARG NEXUS_PASSWORD
# Working directory for Gradle
WORKDIR /app

# Copy project files
COPY . .

# Configure Gradle to use credentials
RUN echo "org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8" >> gradle.properties
RUN echo "systemProp.http.auth.user=$NEXUS_USER" >> gradle.properties
RUN echo "systemProp.http.auth.password=$NEXUS_PASSWORD" >> gradle.properties

# Run Gradle build
RUN gradle clean compileJava --no-daemon