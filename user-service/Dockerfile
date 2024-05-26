FROM openjdk:11
EXPOSE 8001
ADD target/user-service-0.0.1-SNAPSHOT.jar user-service-0.0.1.jar

## Set the working directory inside the container
#WORKDIR /app
#
## Install curl (if not already installed) to download artifacts
#RUN apk --no-cache add curl
#
## Download the Spring Boot application JAR file from Nexus
#RUN curl -u admin:nexus "http://172.20.10.5:8081/repository/com/example/user-service/0.0.1-SNAPSHOT/0.0.1-20240512.223416-19.jar" --output /app/0.0.1-20240512.223416-191.jar

ENTRYPOINT ["java","-jar","user-service-0.0.1.jar"]