FROM gradle:7.0.2-jdk11
WORKDIR /app/

COPY . .

RUN ./gradlew clean build jar

RUN mv build/libs/kasher* /app/app.jar

ENTRYPOINT [ "java", "-jar", "app.jar" ]

