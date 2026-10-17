# Etape 1 : build
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY .mvn .mvn
COPY ./mvnw pom.xml ./
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw
COPY src src
# RUN ./mvnw -q clean package -DskipTests
RUN --mount=type=cache,target=/root/.m2\
    ./mvnw -q clean package -DskipTests \
     -Dmaven.wagon.http.retryHandler.count=5

# Etape 2 : execution
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java","-jar","app.jar"]


