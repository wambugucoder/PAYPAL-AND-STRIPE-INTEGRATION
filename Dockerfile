# For Java 8, try this
FROM openjdk:8-jdk-alpine
# Refer to Maven build -> finalName
ARG JAR_FILE=target/server-0.0.1-SNAPSHOT.jar
# Place working directory on \server
WORKDIR /server
# cp target/server-0.0.1-SNAPSHOT.jar /server/server.jar
COPY ${JAR_FILE} server.jar
# Expose port 8443
EXPOSE 8443
# java -jar /app/app.jar
ENTRYPOINT ["java","-jar","server.jar"]
