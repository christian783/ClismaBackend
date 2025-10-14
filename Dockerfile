# --- Stage 1: build with Maven ---
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /workspace

# copy only what we need first for better caching
COPY pom.xml .
RUN mvn -B dependency:go-offline

# copy source and build
COPY src ./src
RUN mvn -B -DskipTests package

# --- Stage 2: run with a small JRE ---
FROM eclipse-temurin:17-jre
WORKDIR /app

# copy jar from build stage (assumes one jar in target)
COPY --from=build /workspace/target/*.jar app.jar

# expose the port your app uses (your app.properties sets 8081)
EXPOSE 8082

ENTRYPOINT ["java","-jar","/app/app.jar"]
