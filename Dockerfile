FROM eclipse-temurin:17-jdk-alpine
MAINTAINER mafuba
COPY target/cloud-file-storage-*.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]