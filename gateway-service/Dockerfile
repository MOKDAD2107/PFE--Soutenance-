# Etape 1:
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY .mvn .mvn
COPY ./mvnw pom.xml ./
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw
##RUN ./mvnw -q dependency:go-offline
COPY ./src ./src
#RUN ./mvnw -q clean packages -DskipTests
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw -q clean package -DskipTests \
    -Dmaven.wagon.http.retryHandler.count=5
# Etape 2:
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8888
ENTRYPOINT ["java","-jar","app.jar"]
