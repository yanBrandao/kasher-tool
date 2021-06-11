package kafka

import br.com.brandao.kafka.KafkaFactory
import br.com.brandao.kafka.KafkaMessage
import br.com.brandao.kafka.KafkaProperties
import org.apache.kafka.common.header.internals.RecordHeader
import org.apache.kafka.common.serialization.StringSerializer
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class KafkaFactoryUnitTest {

    @Test
    fun `should create kafka producer with all parameters`() {
        val producer = KafkaFactory().create(
            KafkaProperties(
                kafkaBroker = "localhost:9092",
                clientId = "clientId",
                keySerializer = StringSerializer::class,
                valueSerializer = StringSerializer::class
            )
        )
        Assertions.assertNotNull(producer)
    }

    @Test
    fun `should create kafka producer only broker`() {
        val producer = KafkaFactory().create(
            KafkaProperties(kafkaBroker = "localhost:9092")
        )
        Assertions.assertNotNull(producer)
    }

    @Test
    fun `should create producer record with topic and data`() {
        val record = KafkaFactory().create(
            KafkaMessage(
                topic = "topic",
                data = "data"
            )
        )
        Assertions.assertNotNull(record)
    }

    @Test
    fun `should create producer record with topic, data and headers`() {
        val record = KafkaFactory().create(
            KafkaMessage(
                topic = "topic",
                data = "data",
                headers = listOf()
            )
        )
        Assertions.assertNotNull(record)
    }

    @Test
    fun `verify topic in producer record`() {
        val topic = "topic-one"
        val record = KafkaFactory().create(
            KafkaMessage(
                topic = topic,
                data = "data"
            )
        )
        Assertions.assertEquals(topic, record.topic())
    }

    @Test
    fun `verify data in producer record`() {
        val data = "{ any : any}"
        val record = KafkaFactory().create(
            KafkaMessage(
                topic = "topic",
                data = data
            )
        )
        Assertions.assertEquals(data, record.value())
    }

    @Test
    fun `verify header in producer record`() {
        val header = RecordHeader("key", "value".toByteArray())
        val record = KafkaFactory().create(
            KafkaMessage(
                topic = "topic",
                data = "data",
                headers = listOf(header)
            )
        )
        Assertions.assertNotNull(record.headers())
        Assertions.assertNotNull(record.headers().headers("key"))
    }
}