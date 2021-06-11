package br.com.brandao.rest

import br.com.brandao.kafka.KasherProducer
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import javax.validation.Valid

@Validated
@Controller("/api")
class KafkaController(
    private val producer: KasherProducer
) {

    private val log = LoggerFactory.getLogger(javaClass)!!

    @Post(uri = "/{topic}/producer")
    fun publish(@PathVariable("topic") topic: String, @Valid @Body message: KafkaAdapter) {
        log.info("Received new request with message: $message")
        producer.publish(message.toKafkaProperties(), message.toMessage(topic))
    }

}
