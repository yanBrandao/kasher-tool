FROM gradle:7.0.2-jdk11
WORKDIR app/

COPY . .

RUN ./gradlew build

CMD ./gradlew run --args="send-message-topic mensagem_test 1"

