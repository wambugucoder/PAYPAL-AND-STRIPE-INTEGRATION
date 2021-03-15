# For Java 8, try this
FROM maven:3.6.0-jdk-8

# Place working directory on \server
WORKDIR /server

# copy dependency file
COPY pom.xml .

# Copy source file
COPY ./src /server/src


