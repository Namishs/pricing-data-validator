# ---- build stage ----
FROM maven:3.9.4-eclipse-temurin-21 as build
WORKDIR /workspace

# copy maven files first for caching
COPY pom.xml .
# if you have mvnw and .mvn folder, copy them (harmless if missing)
COPY mvnw .
COPY .mvn .mvn
RUN --mount=type=cache,target=/root/.m2 mvn -B -q -DskipTests dependency:go-offline

# copy sources and build
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn -B -q -DskipTests package

# ---- runtime stage ----
FROM eclipse-temurin:21-jre-jammy
ARG JAR_FILE=target/*.jar
WORKDIR /app

# copy jar from build stage
COPY --from=build /workspace/target/*.jar app.jar

# expose port used by spring boot
EXPOSE 8080

# recommended JVM options (tweak as needed)
ENV JAVA_OPTS="-Xms256m -Xmx1024m -Djava.security.egd=file:/dev/./urandom"

ENTRYPOINT ["/bin/sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
