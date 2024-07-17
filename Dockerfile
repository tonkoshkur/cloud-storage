FROM openjdk:21-jdk-slim AS build
ENV HOME /app
WORKDIR $HOME
COPY . $HOME
RUN --mount=type=cache,target=/root/.m2 ./mvnw -f "$HOME"/pom.xml clean package -DskipTests

FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=build /app/target/*.jar /app/app.jar
ENTRYPOINT java -jar app.jar