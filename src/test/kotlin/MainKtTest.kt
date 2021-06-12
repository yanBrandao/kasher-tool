import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import kafka.KasherProducer
import org.apache.kafka.clients.producer.MockProducer
import org.apache.kafka.common.serialization.StringSerializer
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class MainKtTest {

    private val producer: KasherProducer = mock()
    private val kafkaProducer = MockProducer(true, StringSerializer(), StringSerializer())

    @BeforeEach
    fun setup() {
        whenever(producer.create(any(), any())).thenReturn(kafkaProducer)
    }

    @Test
    fun `Should call main function with test parameter`() {
        ProjectMainKt(producer).main(arrayOf("--help"))
    }

    @Test
    fun `Should call main function with all parameters`() {
        ProjectMainKt(producer).main(arrayOf("-b", "localhost:9092","-c", "client-1", "-t", "some-topic", "-d", "\"some message\""))
    }

    @Test
    fun `Should call main function with single headers`() {
        ProjectMainKt(producer).main(arrayOf("-b", "localhost:9092", "-t", "some-topic", "-d", "\"some message\"", "-h", "transaction=123"))
    }

    @Test
    fun `Should call main function with multiples headers`() {
        ProjectMainKt(producer).main(arrayOf("-b", "localhost:9092", "-t", "some-topic", "-d", "\"some message\"", "-h", "transaction=123", "-h", "name=test"))
    }

    @Test
    fun `Should throw exception main function with header without value`() {
        assertThrows<RuntimeException> { ProjectMainKt(producer).main(arrayOf("-b", "localhost:9092", "-t", "some-topic", "-d", "\"some message\"", "-h", "transaction=123", "-h")) }
    }

    @Test
    fun `Should throw exception when miss required topic parameter`() {
        assertThrows<RuntimeException> { ProjectMainKt(producer).main(arrayOf("-b", "localhost:9092", "-d", "\"some message\"", "-h", "transaction=123")) }
    }

    @Test
    fun `Should throw exception when miss required broker parameter`() {
        assertThrows<RuntimeException> { ProjectMainKt(producer).main(arrayOf("-t", "some-topic", "-d", "\"some message\"", "-h", "transaction=123")) }
    }

    @Test
    fun `Should throw exception when miss required message parameter`() {
        assertThrows<RuntimeException> { ProjectMainKt(producer).main(arrayOf("-b", "localhost:9092", "-t", "some-topic")) }
    }

    @Test
    fun `Should throw exception when miss value`() {
        assertThrows<RuntimeException> { ProjectMainKt(producer).main(arrayOf("-b", "localhost:9092", "-t", "some-topic", "-d")) }
    }

}