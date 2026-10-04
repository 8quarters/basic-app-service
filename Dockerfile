FROM eclipse-temurin:17-jdk-jammy

COPY target/basic-app-service-0.0.1-SNAPSHOT.jar /tmp/basic-app-service.jar

CMD ["java", "-jar", "/tmp/basic-app-service.jar"]
