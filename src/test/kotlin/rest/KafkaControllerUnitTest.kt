package rest

import br.com.brandao.kafka.KasherProducer
import br.com.brandao.rest.Header
import br.com.brandao.rest.KafkaAdapter
import br.com.brandao.rest.KafkaController
import io.mockk.mockk
import io.mockk.verify
import org.apache.kafka.common.serialization.ByteArraySerializer
import org.junit.jupiter.api.Test

class KafkaControllerUnitTest {

    private val producer: KasherProducer = mockk(relaxed = true)

    @Test
    fun `kafkaController#publish calls producer#publish`() {
        val adapter = KafkaAdapter(
            broker = "broker",
            clientId = "test",
            data = "{key : value}",
            headers = listOf(
                Header(
                    name = "unit",
                    value = "test"
                )
            ),
            keySerializer = ByteArraySerializer::class.java.name,
            valueSerializer = ByteArraySerializer::class.java.name
        )
        KafkaController(producer).publish("unit-test", adapter)

        verify(exactly = 1) { producer.publish(any(), any()) }
    }
}