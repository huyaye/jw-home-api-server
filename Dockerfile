FROM openjdk:11-jre-slim-buster
ARG JAR_FILE=target/*.jar
RUN mkdir /server
COPY ${JAR_FILE} /server/jw-home-api-server.jar

ENTRYPOINT java -Dspring.profiles.active=develop -jar /server/jw-home-api-server.jar