FROM openjdk:11-jre-slim-buster
ARG JAR_FILE=target/*.jar
ARG PROPERTIES_SECRET_SENSITIVE=external.yml

RUN mkdir /server
COPY ${JAR_FILE} /server/jw-home-api-server.jar
COPY ${PROPERTIES_SECRET_SENSITIVE} /server/external.yml

ENTRYPOINT java -Dspring.profiles.active=develop -jar -Dspring.config.additional-location=/server/external.yml /server/jw-home-api-server.jar