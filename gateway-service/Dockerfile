FROM openjdk:11
EXPOSE 8888
ADD target/gateway-service-0.0.1-SNAPSHOT.jar gateway-service.jar
ENTRYPOINT ["java" , "-jar" , "gateway-service.jar"]
