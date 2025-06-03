FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN apt-get update && apt-get install -y maven \
    && mvn clean package -DskipTests

EXPOSE 8080

# [target/*.jar] could change to the specific jar file name [Tocheck]
CMD ["java", "-jar", "target/crowdfunding-0.0.1-SNAPSHOT.jar"]
