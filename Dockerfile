FROM openjdk:11
EXPOSE 8083
ADD target/parking-lot-app.jar parking-lot-app.jar
ENTRYPOINT ["java", "-jar","/parking-lot-app.jar"]