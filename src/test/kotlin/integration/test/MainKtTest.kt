package integration.test

import br.com.brandao.main
import br.com.brandao.parameter.KafkaConstants
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.ByteArrayDeserializer
import org.junit.jupiter.api.*
import org.rnorth.ducttape.unreliables.Unreliables
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.shaded.org.apache.commons.lang.RandomStringUtils
import org.testcontainers.utility.DockerImageName
import java.time.Duration
import java.util.*
import java.util.concurrent.TimeUnit

@MicronautTest
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MainKtTest {

    private val topic = "kasher-sample-topic"
    private val kafkaTestImage = DockerImageName.parse("confluentinc/cp-kafka:6.1.1")

    @Container
    private val kafka = KafkaContainer(kafkaTestImage)
        .withEmbeddedZookeeper()

    @BeforeAll
    fun beforeAll() {
        kafka.start()

        AdminClient.create(
            mapOf<String, Any>(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG to kafka.bootstrapServers)
        ).use {
            it.createTopics(
                listOf(NewTopic(topic, 1, 1))
            ).all()[30, TimeUnit.SECONDS]
        }
    }

    @AfterAll
    fun afterAll() {
        kafka.close()
    }

    @Nested
    inner class ByCommandLine {

        @BeforeEach
        fun before() {
            System.setProperty("MODE", "COMMAND_LINE")
        }

        @Test
        fun `Should call main function with test parameter`() {
            main(arrayOf("--help"))
            Assertions.assertNull(consume())
        }

        @Test
        fun `Should call main function with all parameters`() {
            val content = RandomStringUtils.randomAlphabetic(20)
            main(
                arrayOf(
                    KafkaConstants.KAFKA_BROKER_PARAMETER,
                    kafka.bootstrapServers,
                    KafkaConstants.CLIENT_ID_PARAMETER,
                    "client-1",
                    KafkaConstants.TOPIC_PARAMETER,
                    topic,
                    KafkaConstants.DATA_MESSAGE_PARAMETER,
                    content
                )
            )
            val records = consume()
            Assertions.assertNotNull(records)
            Assertions.assertFalse(records!!.isEmpty)

            val record = records.first()!!
            Assertions.assertEquals(content, String(record.value()))
        }

        @Test
        fun `Should call main function with minimum parameters`() {
            val content = RandomStringUtils.randomAlphabetic(20)
            main(
                arrayOf(
                    KafkaConstants.KAFKA_BROKER_PARAMETER,
                    kafka.bootstrapServers,
                    KafkaConstants.TOPIC_PARAMETER,
                    topic,
                    KafkaConstants.DATA_MESSAGE_PARAMETER,
                    content
                )
            )
            val records = consume()
            Assertions.assertNotNull(records)
            Assertions.assertFalse(records!!.isEmpty)

            val record = records.first()!!
            Assertions.assertEquals(content, String(record.value()))
        }

        @Test
        fun `Should call main function with single headers`() {
            val headerKey = "transaction"
            val headerValue = "123"
            main(
                arrayOf(
                    KafkaConstants.KAFKA_BROKER_PARAMETER,
                    kafka.bootstrapServers,
                    KafkaConstants.CLIENT_ID_PARAMETER,
                    "client-1",
                    KafkaConstants.TOPIC_PARAMETER,
                    topic,
                    KafkaConstants.DATA_MESSAGE_PARAMETER,
                    RandomStringUtils.randomAlphabetic(20),
                    KafkaConstants.HEADER_PARAMETER,
                    "$headerKey${KafkaConstants.HEADER_PARAMETER_SEPARATOR}$headerValue"
                )
            )
            val records = consume()
            Assertions.assertNotNull(records)
            Assertions.assertFalse(records!!.isEmpty)

            val headers = records.first()!!.headers()
            Assertions.assertNotNull(headers)

            val header = headers.headers(headerKey)
            Assertions.assertNotNull(header)
            Assertions.assertEquals(headerValue, String(header.first().value()))
        }

        @Test
        fun `Should call main function with multiples headers`() {
            val firstHeaderKey = "transaction"
            val firstHeaderValue = "123"

            val secondHeaderKey = "any"
            val secondHeaderValue = "header"

            main(
                arrayOf(
                    KafkaConstants.KAFKA_BROKER_PARAMETER,
                    kafka.bootstrapServers,
                    KafkaConstants.CLIENT_ID_PARAMETER,
                    "client-1",
                    KafkaConstants.TOPIC_PARAMETER,
                    topic,
                    KafkaConstants.DATA_MESSAGE_PARAMETER,
                    RandomStringUtils.randomAlphabetic(20),
                    KafkaConstants.HEADER_PARAMETER,
                    "$firstHeaderKey${KafkaConstants.HEADER_PARAMETER_SEPARATOR}$firstHeaderValue",
                    KafkaConstants.HEADER_PARAMETER,
                    "$secondHeaderKey${KafkaConstants.HEADER_PARAMETER_SEPARATOR}$secondHeaderValue",
                )
            )
            val records = consume()
            Assertions.assertNotNull(records)
            Assertions.assertFalse(records!!.isEmpty)

            val headers = records.first()!!.headers()
            Assertions.assertNotNull(headers)

            val firstHeader = headers.headers(firstHeaderKey)
            Assertions.assertNotNull(firstHeader)
            Assertions.assertEquals(firstHeaderValue, String(firstHeader.first().value()))

            val secondHeader = headers.headers(secondHeaderKey)
            Assertions.assertNotNull(secondHeader)
            Assertions.assertEquals(secondHeaderValue, String(secondHeader.first().value()))
        }

        @Test
        fun `Should throw exception main function with header without value`() {
            assertThrows<RuntimeException> {
                main(
                    arrayOf(
                        KafkaConstants.KAFKA_BROKER_PARAMETER,
                        kafka.bootstrapServers,
                        KafkaConstants.CLIENT_ID_PARAMETER,
                        "client-1",
                        KafkaConstants.TOPIC_PARAMETER,
                        topic,
                        KafkaConstants.DATA_MESSAGE_PARAMETER,
                        RandomStringUtils.randomAlphabetic(20),
                        KafkaConstants.HEADER_PARAMETER
                    )
                )
            }
        }

        @Test
        fun `Should throw exception when miss required topic parameter`() {
            assertThrows<RuntimeException> {
                main(
                    arrayOf(
                        KafkaConstants.KAFKA_BROKER_PARAMETER,
                        kafka.bootstrapServers,
                        KafkaConstants.DATA_MESSAGE_PARAMETER,
                        RandomStringUtils.randomAlphabetic(20)
                    )
                )
            }
        }

        @Test
        fun `Should throw exception when miss required broker parameter`() {
            assertThrows<RuntimeException> {
                main(
                    arrayOf(
                        KafkaConstants.TOPIC_PARAMETER,
                        topic,
                        KafkaConstants.DATA_MESSAGE_PARAMETER,
                        RandomStringUtils.randomAlphabetic(20)
                    )
                )
            }
        }

        @Test
        fun `Should throw exception when miss required message parameter`() {
            assertThrows<RuntimeException> {
                main(
                    arrayOf(
                        KafkaConstants.KAFKA_BROKER_PARAMETER,
                        kafka.bootstrapServers,
                        KafkaConstants.TOPIC_PARAMETER,
                        topic
                    )
                )
            }
        }

        @Test
        fun `Should throw exception when miss value`() {
            assertThrows<RuntimeException> {
                main(
                    arrayOf(
                        KafkaConstants.KAFKA_BROKER_PARAMETER,
                        kafka.bootstrapServers,
                        KafkaConstants.TOPIC_PARAMETER,
                        topic,
                        KafkaConstants.DATA_MESSAGE_PARAMETER
                    )
                )
            }
        }
    }

    private fun consume(): ConsumerRecords<ByteArray, ByteArray>? {
        return KafkaConsumer(
            mapOf<String, Any>(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to kafka.bootstrapServers,
                ConsumerConfig.GROUP_ID_CONFIG to "tc-" + UUID.randomUUID(),
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "earliest"
            ),
            ByteArrayDeserializer(),
            ByteArrayDeserializer()
        ).use {
            it.subscribe(listOf(topic))
            val records = Unreliables.retryUntilSuccess(10, TimeUnit.SECONDS) {
                val found = it.poll(Duration.ofMillis(100))
                if (found.isEmpty) {
                    throw RuntimeException("Message not found")
                }
                found
            }
            it.unsubscribe()
            return records
        }
    }

}