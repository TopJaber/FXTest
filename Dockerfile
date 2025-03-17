FROM openjdk:21
WORKDIR /app
COPY target/app.zip /app/fxtest.jar
ENTRYPOINT ["java", "-jar", "fxtest.jar"]