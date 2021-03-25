FROM maven:3.5.3-jdk-8-alpine AS MAVEN_BUILD

MAINTAINER Jos Wambugu

WORKDIR /build

COPY . .

RUN mvn package


FROM openjdk:8-jre-alpine

WORKDIR /app

COPY --from=MAVEN_BUILD /build/target/server-0.0.1-SNAPSHOT.jar  /app/

ENTRYPOINT ["java", "-jar", "server-0.0.1-SNAPSHOT.jar"]