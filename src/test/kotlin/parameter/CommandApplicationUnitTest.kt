package parameter

import br.com.brandao.kafka.KasherProducer
import br.com.brandao.parameter.CommandApplication
import br.com.brandao.parameter.KafkaConstants
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CommandApplicationUnitTest {

    private lateinit var producer: KasherProducer

    @BeforeEach
    fun before() {
        producer = mockk(relaxed = true)
    }

    @Test
    fun `commandApplication#run does not call kasherProducer#publish when help parameter is present`() {
        CommandApplication(producer).run(arrayOf(KafkaConstants.HELP_PARAMETER))
        verify(exactly = 0) { producer.publish(any(), any()) }
    }

    @Test
    fun `commandApplication#run calls kasherProducer#publish when parameter is present`() {
        val args = arrayOf(
            KafkaConstants.KAFKA_BROKER_PARAMETER, "broker",
            KafkaConstants.TOPIC_PARAMETER, "topic",
            KafkaConstants.DATA_MESSAGE_PARAMETER, "{data : value}"
        )

        CommandApplication(producer).run(args)
        verify(exactly = 1) { producer.publish(any(), any()) }
    }
}