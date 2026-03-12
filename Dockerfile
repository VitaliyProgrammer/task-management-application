FROM eclipse-temurin:17-jdk-jammy

WORKDIR /application

RUN apt-get update && \
    apt-get install -y ca-certificates-java && \
    update-ca-certificates

COPY target/task-management-application-1.0.1.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]