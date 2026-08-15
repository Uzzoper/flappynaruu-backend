# =========================
# Build stage
# =========================
FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

# =========================
# Prepare stage: extract layers + assemble exploded layout
# =========================
FROM eclipse-temurin:17-jre AS prepare

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

# Extract the fat JAR into a layered layout (faster class loading at runtime)
# and assemble the runtime layout: slim launcher jar + dependency jars in lib/
RUN java -Djarmode=tools -jar app.jar extract --layers --destination extracted \
    && cp extracted/application/*.jar . \
    && cp -r extracted/dependencies/lib ./lib

# =========================
# Runtime stage
# =========================
FROM eclipse-temurin:17-jre

WORKDIR /app

RUN useradd -m appuser

COPY --from=prepare /app/app.jar ./
COPY --from=prepare /app/lib/ ./lib/

# Training run: generate the CDS archive from the exact files that will run
# (same layer, same mtimes -> archive compatible). DB-dependent startup steps
# are disabled (dummy env vars + flags) so the build does not need a database.
RUN SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/flappynaruu \
    SPRING_DATASOURCE_USERNAME=flappynaruu \
    SPRING_DATASOURCE_PASSWORD=flappynaruu \
    LEADERBOARD_SECRET=training \
    java -XX:ArchiveClassesAtExit=application.jsa -Dspring.context.exit=onRefresh \
    -jar app.jar \
    --spring.datasource.hikari.initialization-fail-timeout=-1 \
    --spring.datasource.hikari.connection-timeout=2000 \
    --spring.flyway.enabled=false \
    --spring.jpa.hibernate.ddl-auto=none \
    --spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect

USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-XX:SharedArchiveFile=application.jsa", "-XX:TieredStopAtLevel=1", "-XX:+UseSerialGC", "-XX:MaxRAMPercentage=70.0", "-Xss512k", "-jar", "app.jar"]