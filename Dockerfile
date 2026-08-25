# ---------- Stage 1: Build ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build

# Copy pom.xml first (better layer caching — deps only re-download if pom changes)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source and build
COPY src ./src
RUN mvn clean package -DskipTests

# ---------- Stage 2: Runtime ----------
FROM tomcat:10.1-jdk21-temurin AS runtime

# Build metadata (passed in at build time)
ARG APP_VERSION=1.0.0
ARG BUILD_NUMBER=1
ARG BUILD_DATE

LABEL app.name="demo-backend" \
      app.version="${APP_VERSION}" \
      app.build="${BUILD_NUMBER}" \
      app.build_date="${BUILD_DATE}"

# Clean default Tomcat apps, deploy ours
RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=build /build/target/app.war /usr/local/tomcat/webapps/app.war

# Bake version info into a file inside the container too (handy for debugging)
RUN echo "version=${APP_VERSION}" > /usr/local/tomcat/webapps/app-version.txt && \
    echo "build=${BUILD_NUMBER}" >> /usr/local/tomcat/webapps/app-version.txt && \
    echo "date=${BUILD_DATE}" >> /usr/local/tomcat/webapps/app-version.txt

EXPOSE 8080
CMD ["catalina.sh", "run"]
