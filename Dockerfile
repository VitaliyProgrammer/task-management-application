# Multi-stage build: the hosting platform builds the image straight from the repository,
# so the jar is compiled inside Docker instead of being copied from a local target/ folder.

# ===== 1. Build =====
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /build

# Dependencies first: this layer is cached until pom.xml changes
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src
# Tests and checkstyle already run in CI on every pull request
RUN mvn -B -q package -DskipTests -Dcheckstyle.skip

# ===== 2. Run =====
FROM eclipse-temurin:17-jre-jammy

WORKDIR /application

# Never run the app as root inside the container
RUN useradd --system --uid 1001 spring
USER spring

COPY --from=build /build/target/task-management-application-1.0.1.jar app.jar

EXPOSE 8080

# Free instances have 512 MB: let the heap use 75% of the container memory and use the
# serial GC, which has the smallest memory overhead on a single CPU
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-XX:+UseSerialGC", "-jar", "app.jar"]
