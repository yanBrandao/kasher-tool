package kafka

import kafka.KafkaConstants.Companion.CLIENT_ID
import kafka.KafkaConstants.Companion.KAFKA_BROKERS
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerConfig.*
import org.apache.kafka.common.serialization.LongSerializer
import org.apache.kafka.common.serialization.StringSerializer
import java.util.*


class KasherProducer {
    companion object{
        fun create(): Producer<Long, String> {
            val props = Properties()
            props[BOOTSTRAP_SERVERS_CONFIG] = KAFKA_BROKERS
            props[CLIENT_ID_CONFIG] = CLIENT_ID
            props[KEY_SERIALIZER_CLASS_CONFIG] = LongSerializer::class.java.name
            props[VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java.name
            return KafkaProducer(props)
        }
    }
}