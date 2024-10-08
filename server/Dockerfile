# Dockerfile

FROM openjdk:17-jdk-slim
LABEL authors="Mangjun"

COPY build/libs/server-0.0.1-SNAPSHOT.jar recoder.jar

ENTRYPOINT ["java", "-jar", "/recoder.jar"]