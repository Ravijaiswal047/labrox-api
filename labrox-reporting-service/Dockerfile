FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
ARG JAR_FILE=target/*-exec.jar
COPY ${JAR_FILE} app.jar
ENV JAVA_OPTS="-XX:+UseG1GC -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"
EXPOSE 8091
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
