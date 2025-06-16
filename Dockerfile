FROM openjdk:21-jdk-slim
LABEL authors="firane"
WORKDIR /app
COPY target/chemist-BE-0.0.1-SNAPSHOT.jar chemist-BE-0.0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "chemist-BE-0.0.1-SNAPSHOT.jar"]