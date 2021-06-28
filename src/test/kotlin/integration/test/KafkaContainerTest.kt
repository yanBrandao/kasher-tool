package integration.test

import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.junit.ClassRule
import org.junit.jupiter.api.Test
import org.rnorth.ducttape.unreliables.Unreliables
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.containers.Network
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap
import org.testcontainers.utility.DockerImageName
import java.time.Duration
import java.util.*
import java.util.concurrent.TimeUnit

class KafkaContainerTest {

    private val KAFKA_TEST_IMAGE = DockerImageName.parse("confluentinc/cp-kafka:6.1.1")
    private val ZOOKEEPER_TEST_IMAGE = DockerImageName.parse("confluentinc/cp-zookeeper:6.1.1")

    // junitRule {
    @ClassRule
    var kafka = KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:6.1.1"))
    // }

    // }
    @Test
    @Throws(Exception::class)
    fun testUsage() {
        KafkaContainer(KAFKA_TEST_IMAGE).use { kafka ->
            kafka.start()
            testKafkaFunctionality(kafka.bootstrapServers)
        }
    }


    @Test
    @Throws(Exception::class)
    fun testUsageWithSpecificImage() {
        KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:6.1.1")).use { kafka ->
            kafka.start()
            testKafkaFunctionality( // getBootstrapServers {
                kafka.bootstrapServers // }
            )
        }
    }


    @Test
    @Throws(Exception::class)
    fun testUsageWithVersion() {
        KafkaContainer("5.5.1").use { kafka ->
            kafka.start()
            testKafkaFunctionality(kafka.bootstrapServers)
        }
    }

    @Test
    @Throws(Exception::class)
    fun testExternalZookeeperWithExternalNetwork() {
        Network.newNetwork().use { network ->
            KafkaContainer(KAFKA_TEST_IMAGE)
                .withNetwork(network)
                .withExternalZookeeper("zookeeper:2181").use { kafka ->
                    GenericContainer<GenericContainer<*>>(ZOOKEEPER_TEST_IMAGE)
                        .withNetwork(network)
                        .withNetworkAliases("zookeeper")
                        .withEnv("ZOOKEEPER_CLIENT_PORT", "2181").use { zookeeper ->
                            GenericContainer<GenericContainer<*>>(DockerImageName.parse("alpine"))
                                .withNetwork(network) // }
                                .withNetworkAliases("dummy")
                                .withCommand("sleep 10000").use { application ->
                                    zookeeper.start()
                                    kafka.start()
                                    application.start()
                                    testKafkaFunctionality(kafka.bootstrapServers)
                                }
                        }
                }
        }
    }

    @Test
    @Throws(Exception::class)
    fun testConfluentPlatformVersion6() {
        KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:6.1.1")).use { kafka ->
            kafka.start()
            testKafkaFunctionality(kafka.bootstrapServers)
        }
    }

    @Throws(Exception::class)
    protected fun testKafkaFunctionality(bootstrapServers: String?) {
        testKafkaFunctionality(bootstrapServers, 1, 1)
    }

    @Throws(Exception::class)
    protected fun testKafkaFunctionality(bootstrapServers: String?, partitions: Int, rf: Int) {
        AdminClient.create(
            ImmutableMap.of<String, Any?>(
                AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers
            )
        ).use { adminClient ->
            KafkaProducer(
                ImmutableMap.of<String, Any?>(
                    ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                    ProducerConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString()
                ),
                StringSerializer(),
                StringSerializer()
            ).use { producer ->
                KafkaConsumer(
                    ImmutableMap.of<String, Any?>(
                        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                        ConsumerConfig.GROUP_ID_CONFIG, "tc-" + UUID.randomUUID(),
                        ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"
                    ),
                    StringDeserializer(),
                    StringDeserializer()
                ).use { consumer ->
                    val topicName = "messages-" + UUID.randomUUID()
                    val topics: Collection<NewTopic> =
                        listOf(NewTopic(topicName, partitions, rf.toShort()))
                    adminClient.createTopics(topics).all()[30, TimeUnit.SECONDS]
                    consumer.subscribe(listOf(topicName))
                    producer.send(ProducerRecord(topicName, "testcontainers", "rulezzz"))
                        .get()
                    Unreliables.retryUntilTrue(10, TimeUnit.SECONDS) {
                        val records =
                            consumer.poll(Duration.ofMillis(100))
                        if (records.isEmpty) {
                            return@retryUntilTrue false
                        }
                        true
                    }
                    consumer.unsubscribe()
                }
            }
        }
    }
}