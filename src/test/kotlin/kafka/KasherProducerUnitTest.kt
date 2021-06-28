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
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class KasherProducerUnitTest {

    private val properties = KafkaProperties(kafkaBroker = "unit-test")
    private val message = KafkaMessage(topic = "topic", data = "data")
    private val timeout = 60L

    private lateinit var kafkaProducer: KafkaProducer<ByteArray, ByteArray>
    private lateinit var record: ProducerRecord<ByteArray, ByteArray>
    private lateinit var factory: KafkaFactory
    private lateinit var producer: KasherProducer

    @BeforeEach
    fun before() {
        factory = mockk(relaxed = true)
        kafkaProducer = mockk(relaxed = true)
        record = mockk(relaxed = true)

        every { factory.create(properties) } returns kafkaProducer
        every { factory.create(message) } returns record

        producer = KasherProducer(factory, timeout)
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
    fun `producer must close kafkaProducer when exception has occurred`() {
        producer.publish(properties, message)

        every { producer.publish(any(), any()) } throws IllegalArgumentException()
        assertThrows<IllegalArgumentException> { producer.publish(properties, message) }

        verify (exactly = 1) { kafkaProducer.close() }
    }

    @Test
    fun `producer rethrow any exception`() {
        every { producer.publish(any(), any()) } throws IllegalArgumentException()

        assertThrows<IllegalArgumentException> { producer.publish(properties, message) }
    }
}