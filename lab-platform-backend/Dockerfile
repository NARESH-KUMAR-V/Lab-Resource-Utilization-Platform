FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline -B

COPY src ./src

RUN ./mvnw clean package -DskipTests

EXPOSE 8080

CMD ["java", "-Xms128m", "-Xmx320m", "-XX:MaxMetaspaceSize=128m", "-XX:ReservedCodeCacheSize=64m", "-Xss512k", "-XX:+TieredCompilation", "-XX:TieredStopAtLevel=1", "-jar", "target/lab-platform-backend-0.0.1-SNAPSHOT.jar"]