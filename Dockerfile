# For Java 8, try this
FROM maven:3.5.3-jdk-8-alpine

# Place working directory on \server
WORKDIR /server

# copy dependency file
COPY pom.xml .

# Resolve dependencies
RUN mvn dependency:go-offline -B

# Copy source file
COPY ./src  /server/src

# java -jar /app/app.jar
RUN mvn package
