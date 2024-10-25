FROM openjdk:17.0.2-slim

WORKDIR /app

ARG JAR_FILE=build/libs/teacher-net-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} /app/teacher-net.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "teacher-net.jar"]
