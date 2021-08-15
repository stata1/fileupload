FROM adoptopenjdk/openjdk11:ubi
VOLUME /main-app
ADD target/file-upload-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-jar","/app.jar"]