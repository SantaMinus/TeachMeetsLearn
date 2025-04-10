# Step 1: Build the application
FROM gradle:8.13.0 as build
WORKDIR /app
COPY --chown=gradle:gradle . /app
RUN gradle clean bootJar --no-daemon

# Step 2: Extract layers from the JAR
FROM openjdk:23-slim as builder
WORKDIR /app
COPY --from=build /app/build/libs/teach-meets-learn-0.0.1-SNAPSHOT.jar /app/teach-meets-learn.jar
RUN java -Djarmode=layertools -jar teach-meets-learn.jar extract

# Step 3: Create the final image with extracted layers
FROM openjdk:23-slim
WORKDIR /app
COPY --from=builder /app/dependencies/ ./
COPY --from=builder /app/spring-boot-loader/ ./
COPY --from=builder /app/snapshot-dependencies/ ./
COPY --from=builder /app/application/ ./

ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]
