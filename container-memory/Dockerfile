FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /src
COPY pom.xml .
COPY src src
RUN mvn -q -DskipTests package

FROM eclipse-temurin:21.0.5_11-jre
COPY --from=build /src/target/container-memory-1.0.0.jar /app.jar
# Heap = 60% of the container limit; the rest is metaspace, thread stacks, direct buffers.
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=60", "-XX:+ExitOnOutOfMemoryError", "-cp", "/app.jar", "lab.MemoryReport"]
