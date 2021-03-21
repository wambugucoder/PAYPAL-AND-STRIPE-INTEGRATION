# For Java 8, try this: maven:3.6.0-jdk-8-alpine
FROM maven:3.5.3-jdk-8-alpine

RUN apk add --no-cache openjdk11
# Place working directory on \server
WORKDIR /server

# copy dependency file
COPY . .

