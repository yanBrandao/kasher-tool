package kafka

import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerConfig.*
import org.apache.kafka.common.serialization.LongSerializer
import org.apache.kafka.common.serialization.StringSerializer
import java.util.*

class KasherProducer {
    companion object {
        fun create(
            kafkaBroker: String,
            clientId: String
        ): Producer<String, String> {
            val props = Properties()
            props[BOOTSTRAP_SERVERS_CONFIG] = kafkaBroker
            props[CLIENT_ID_CONFIG] = clientId
            props[KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java.name
            props[VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java.name
            return KafkaProducer(props)
        }
    }
}