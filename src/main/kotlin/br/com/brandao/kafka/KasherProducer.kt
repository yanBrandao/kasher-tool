package br.com.brandao.kafka

import io.micronaut.context.annotation.Prototype
import io.micronaut.context.annotation.Value
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit

@Prototype
class KasherProducer(
    private val kafkaFactory: KafkaFactory,
    @Value("\${kasher.publish.timeout}") val timeout: Long
) {

    private val log = LoggerFactory.getLogger(javaClass)!!

    fun publish(properties: KafkaProperties, message: KafkaMessage) {
        kafkaFactory.create(properties).use {
            log.info("Publishing new message to kafka")
            log.debug("Message: $message")
            it.send(kafkaFactory.create(message))
                .get(timeout, TimeUnit.SECONDS)
            log.info("Published new message")
        }
    }

}
