package kafka

import org.apache.kafka.common.KafkaException
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows

internal class KasherProducerTest {

    @Test
    fun `Should not create producer without kafka`() {
        val kasherProducer = KasherProducer()

        assertThrows<KafkaException> { kasherProducer.create("broker", "client") }
    }
}