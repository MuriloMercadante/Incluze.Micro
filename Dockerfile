# ---------- Etapa 1: build da aplicacao ----------
FROM maven:3.9.8-eclipse-temurin-17-alpine AS build
WORKDIR /opt/app
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src ./src
RUN mvn -B clean package -DskipTests

# ---------- Etapa 2: imagem de execucao ----------
FROM eclipse-temurin:17-jre-alpine
WORKDIR /opt/app
RUN addgroup -S app && adduser -S app -G app
COPY --from=build /opt/app/target/app.jar /opt/app/app.jar
USER app
ENV SPRING_PROFILES_ACTIVE=prd
EXPOSE 8080
CMD [ "sh", "-c", "java -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE} -jar app.jar" ]
