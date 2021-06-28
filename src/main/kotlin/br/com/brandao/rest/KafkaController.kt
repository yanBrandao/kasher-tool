package br.com.brandao.rest

import br.com.brandao.kafka.KasherProducer
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
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

    @Get(uri = "/help", produces = [MediaType.APPLICATION_JSON])
    fun help() =
        KafkaAdapter(
            broker = "boostrap.server where you broker is located (url:port). Mandatory value",
            clientId = "information for client that make request. Optional",
            data = "Message content. Value must have in quotation marks (\"sample\"). Mandatory value",
            headers = listOf(
                Header(name = "name of header", value = "value of header")
            ),
            keySerializer = "Class full name that will be serialize the key. Optional",
            valueSerializer = "Class full name that will be serialize the value. Optional"
        )

}
