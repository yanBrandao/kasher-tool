package br.com.brandao.kafka

import io.micronaut.context.annotation.Prototype
import org.slf4j.LoggerFactory

@Prototype
class KasherProducer(
    private val kafkaFactory: KafkaFactory
) {

    private val log = LoggerFactory.getLogger(javaClass)!!

    fun publish(properties: KafkaProperties, message: KafkaMessage) {
        kafkaFactory.create(properties).use {
            log.info("Publishing new message to kafka")
            log.debug("Message: $message")
            it.send(kafkaFactory.create(message))
            log.info("Published new message")
        }
    }

}
