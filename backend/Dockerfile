FROM maven:3.9-eclipse-temurin-24-alpine AS builder
ARG APP_NAME=gamestudio-backend

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:24-jdk-alpine 
ARG APP_NAME=gamestudio-backend

WORKDIR /app

COPY --from=builder /app/target/gamestudio.jar ./gamestudio.jar

EXPOSE 8080

CMD ["java", "-jar", "gamestudio.jar"]
