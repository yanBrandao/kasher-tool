package kafka

import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerConfig.*
import org.apache.kafka.common.serialization.ByteArraySerializer
import java.util.*

open class KasherProducer {
        open fun create(
            kafkaBroker: String,
            clientId: String
        ): Producer<ByteArray, ByteArray> {
            val props = Properties()
            props[BOOTSTRAP_SERVERS_CONFIG] = kafkaBroker
            props[CLIENT_ID_CONFIG] = clientId
            props[KEY_SERIALIZER_CLASS_CONFIG] = ByteArraySerializer::class.java.name
            props[VALUE_SERIALIZER_CLASS_CONFIG] = ByteArraySerializer::class.java.name
            return KafkaProducer(props)
        }
}