import kafka.KafkaConstants.Companion.TRANSACTION_KEY
import kafka.KasherProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.header.internals.RecordHeader
import java.time.Duration

fun main(args: Array<String>) {
    val topicName = args[0]
    val message = args[1]
    val header = args[2]
    val producer = KasherProducer.create()

    val record = ProducerRecord<Long, String>(topicName, message)
    record.headers().add(RecordHeader(TRANSACTION_KEY, header.toByteArray()))

    producer.send(record)

    producer.close(Duration.ofSeconds(10))
}