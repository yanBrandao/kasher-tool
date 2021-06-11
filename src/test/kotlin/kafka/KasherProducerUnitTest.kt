package kafka

import br.com.brandao.kafka.KafkaFactory
import br.com.brandao.kafka.KafkaMessage
import br.com.brandao.kafka.KafkaProperties
import br.com.brandao.kafka.KasherProducer
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.KafkaException
import org.apache.kafka.common.header.Header
import org.apache.kafka.common.serialization.Serializer
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.lang.IllegalArgumentException
import kotlin.reflect.KClass

internal class KasherProducerUnitTest {

    private val properties = KafkaProperties(kafkaBroker = "unit-test")
    private val message = KafkaMessage(topic = "topic", data = "data")

    private lateinit var kafkaProducer: KafkaProducer<String, String>
    private lateinit var record: ProducerRecord<String, String>
    private lateinit var factory: KafkaFactory
    private lateinit var producer: KasherProducer

    @BeforeEach
    fun before() {
        factory = mockk(relaxed = true)
        kafkaProducer = mockk(relaxed = true)
        record = mockk(relaxed = true)

        every { factory.create(properties) } returns kafkaProducer
        every { factory.create(message) } returns record

        producer = KasherProducer(factory)
    }

    @Test
    fun `should send message`() {
        producer.publish(properties, message)
    }

    @Test
    fun `producer must calls kafkaProducer#send`() {
        producer.publish(properties, message)

        verify (exactly = 1) { kafkaProducer.send(record) }
    }

    @Test
    fun `producer must close kafkaProducer`() {
        producer.publish(properties, message)

        verify (exactly = 1) { kafkaProducer.close() }
    }

    @Test
    fun `producer rethrow any exception`() {
        every { producer.publish(any(), any()) } throws IllegalArgumentException()

        assertThrows<IllegalArgumentException> { producer.publish(properties, message) }
    }
}