package parameter

import br.com.brandao.parameter.KafkaConstants
import br.com.brandao.parameter.Parameter
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ParameterUnitTest {

    @Test
    fun `parameter#help should return true when args contains --help`() {
        val help = Parameter().help(arrayOf(KafkaConstants.HELP_PARAMETER))
        Assertions.assertTrue(help)
    }

    @Test
    fun `parameter#help should return false when args not contains --help`() {
        val help = Parameter().help(arrayOf())
        Assertions.assertFalse(help)
    }

    @Test
    fun `parameter#broker should return broker value from args`() {
        val broker = Parameter().broker(arrayOf(KafkaConstants.KAFKA_BROKER_PARAMETER, "unit-test"))
        Assertions.assertEquals("unit-test", broker)
    }

    @Test
    fun `parameter#broker throw exception when args not contains broker parameter`() {
        assertThrows<RuntimeException> { Parameter().broker(arrayOf()) }
    }

    @Test
    fun `parameter#broker throw exception when args contains invalid broker value`() {
        assertThrows<RuntimeException> {
            Parameter().broker(
                arrayOf(KafkaConstants.KAFKA_BROKER_PARAMETER, "-unit-test")
            )
        }
    }

    @Test
    fun `parameter#topic should return value from args`() {
        val topic = Parameter().topic(arrayOf(KafkaConstants.TOPIC_PARAMETER, "unit-test"))
        Assertions.assertEquals("unit-test", topic)
    }

    @Test
    fun `parameter#topic throw exception when args not contains value`() {
        assertThrows<RuntimeException> { Parameter().topic(arrayOf()) }
    }

    @Test
    fun `parameter#topic throw exception when args contains invalid value`() {
        assertThrows<RuntimeException> { Parameter().topic(arrayOf(KafkaConstants.TOPIC_PARAMETER, "-unit-test")) }
    }

    @Test
    fun `parameter#data should return value from args`() {
        val data = Parameter().data(arrayOf(KafkaConstants.DATA_MESSAGE_PARAMETER, "{\"unit\" : \"test\"}"))
        Assertions.assertEquals("{\"unit\" : \"test\"}", data)
    }

    @Test
    fun `parameter#data throw exception when args not contains value`() {
        assertThrows<RuntimeException> { Parameter().data(arrayOf()) }
    }

    @Test
    fun `parameter#data throw exception when args contains invalid value`() {
        assertThrows<RuntimeException> {
            Parameter().data(
                arrayOf(KafkaConstants.DATA_MESSAGE_PARAMETER,"-{\"unit\" : \"test\"}")
            )
        }
    }

    @Test
    fun `parameter#clientId should return value from args`() {
        val clientId = Parameter().clientId(arrayOf(KafkaConstants.CLIENT_ID_PARAMETER, "unit-test"))
        Assertions.assertEquals("unit-test", clientId)
    }

    @Test
    fun `parameter#clientId return null when args not contains value`() {
        val clientId = Parameter().clientId(arrayOf())
        Assertions.assertNull(clientId)
    }

    @Test
    fun `parameter#clientId throw exception when args contains invalid value`() {
        assertThrows<RuntimeException> {
            Parameter().clientId(
                arrayOf(KafkaConstants.CLIENT_ID_PARAMETER, "-unit-test")
            )
        }
    }

    @Test
    fun `parameter#header should return value from args`() {
        val headers = Parameter().headers(
            arrayOf(KafkaConstants.HEADER_PARAMETER, "unit${KafkaConstants.HEADER_PARAMETER_SEPARATOR}test")
        )
        Assertions.assertNotNull(headers)
        val header = headers.first()
        Assertions.assertEquals("unit", header.key())
        Assertions.assertEquals("test", String(header.value()))
    }

    @Test
    fun `parameter#header return null when args not contains value`() {
        val headers = Parameter().headers(arrayOf())
        Assertions.assertNotNull(headers)
        Assertions.assertNull(headers.firstOrNull())
    }

    @Test
    fun `parameter#header throw exception when args contains invalid value`() {
        assertThrows<RuntimeException> { Parameter().headers(arrayOf(KafkaConstants.HEADER_PARAMETER, "unit-test")) }
    }

    @Test
    fun `parameter#header throw exception when args contains incorrect value`() {
        assertThrows<RuntimeException> { Parameter().headers(arrayOf(KafkaConstants.HEADER_PARAMETER, "-unit-test")) }
    }
}
