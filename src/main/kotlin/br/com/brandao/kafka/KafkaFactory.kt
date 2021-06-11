package br.com.brandao.kafka

import io.micronaut.context.annotation.Factory
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.header.Header
import org.apache.kafka.common.serialization.ByteArraySerializer
import org.apache.kafka.common.serialization.StringSerializer
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.reflect.KClass

@Factory
class KafkaFactory {

    fun create(properties: KafkaProperties): KafkaProducer<String, String> {
        val log = LoggerFactory.getLogger(javaClass)!!
        log.info("Creating new KafkaProducer")
        log.debug("Kafka properties $properties")

        val props = Properties()
        props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = properties.kafkaBroker
        props[ProducerConfig.CLIENT_ID_CONFIG] = properties.clientId ?: "client-${UUID.randomUUID()}"
        props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] =
            (properties.keySerializer ?: ByteArraySerializer::class).java.name
        props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] =
            (properties.valueSerializer ?: ByteArraySerializer::class).java.name
        return KafkaProducer<String, String>(props)
    }

    fun create(message: KafkaMessage): ProducerRecord<String, String> {
        val record = ProducerRecord<String, String>(message.topic, message.data)
        message.headers?.forEach(record.headers()::add)
        return record
    }
}

data class KafkaProperties(
    val kafkaBroker: String,
    val clientId: String? = null,
    val keySerializer: KClass<StringSerializer>? = null,
    val valueSerializer: KClass<StringSerializer>? = null
)

data class KafkaMessage(
    val topic: String,
    val data: String,
    val headers: Iterable<Header>? = null
)
