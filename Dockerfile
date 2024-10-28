# Step 1: Build the application
FROM gradle:8.4 as build
WORKDIR /app
COPY --chown=gradle:gradle . /app
RUN gradle clean bootJar --no-daemon

# Step 2: Extract layers from the JAR
FROM openjdk:17.0.2-slim as builder
WORKDIR /app
COPY --from=build /app/build/libs/teacher-net-0.0.1-SNAPSHOT.jar /app/teacher-net.jar
RUN java -Djarmode=layertools -jar teacher-net.jar extract

# Step 3: Create the final image with extracted layers
FROM openjdk:17.0.2-slim
WORKDIR /app
COPY --from=builder /app/dependencies/ ./
COPY --from=builder /app/spring-boot-loader/ ./
COPY --from=builder /app/snapshot-dependencies/ ./
COPY --from=builder /app/application/ ./

ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]
