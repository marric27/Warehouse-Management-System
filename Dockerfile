# ==============================
# STAGE 1 - Build con Maven
# ==============================
FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /app

# Copia solo il pom prima (per sfruttare la cache Docker)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copia il resto del progetto
COPY src ./src

# Build dell'app (skip test in fase docker build)
RUN mvn clean package -DskipTests


# ==============================
# STAGE 2 - Runtime leggero
# ==============================
FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

# Copia il jar generato
COPY --from=builder /app/target/warehouse-management-system-0.0.1-SNAPSHOT.jar app.jar

# Espone la porta standard Spring Boot
EXPOSE 8080

# Variabili ambiente (puoi sovrascriverle)
ENV JAVA_OPTS=""

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
