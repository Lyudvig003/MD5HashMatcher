# Stage 1: Gradle Build
FROM gradle:8.4-jdk17 AS build

# Add CA certificate if needed
ARG CERTIFICATE_FILE
COPY $CERTIFICATE_FILE /usr/local/share/ca-certificates/idt.local-CA.crt
RUN update-ca-certificates
RUN keytool -importcert -trustcacerts -keystore $JAVA_HOME/lib/security/cacerts -storepass changeit -noprompt -alias artifactory -file /usr/local/share/ca-certificates/idt.local-CA.crt

# Working directory for Gradle
WORKDIR /app

# Copy project files
COPY . .

# Set the new version based on the script
ARG RELEASE_VERSION_NUMBER
RUN ./gradlew -q version -PreleaseVersion=$RELEASE_VERSION_NUMBER

# Build the project
RUN ./gradlew -q clean build -x test

# Deploy the artifact to Nexus
RUN ./gradlew -q publish -PnexusUrl=https://nexus.idt.local/repository/maven-releases/ -PnexusUser=$NEXUS_USER -PnexusPassword=$NEXUS_PASSWORD --no-daemon

## Stage 2: Runtime Image
FROM eclipse-temurin:17-jre

# Copy the built artifact
COPY --from=build /app/build/libs/*.jar app.jar

# Expose application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
