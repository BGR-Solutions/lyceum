FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml /app/pom.xml
COPY academic/pom.xml /app/academic/pom.xml
COPY notification/pom.xml /app/notification/pom.xml
RUN mvn -q -pl academic -am dependency:go-offline
COPY . /app
RUN mvn -q -pl academic -am package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/academic/target/academic-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
