import kafka.KafkaConstants.Companion.CLIENT_ID_PARAMETER
import kafka.KafkaConstants.Companion.DATA_MESSAGE_PARAMETER
import kafka.KafkaConstants.Companion.HEADER_PARAMETER
import kafka.KafkaConstants.Companion.HEADER_PARAMETER_SEPARATOR
import kafka.KafkaConstants.Companion.KAFKA_BROKER_PARAMETER
import kafka.KafkaConstants.Companion.TOPIC_PARAMETER
import kafka.KasherProducer
import kafka.Parameter
import org.apache.kafka.clients.producer.ProducerRecord

class ProjectMainKt(private val producer: KasherProducer) {

    fun main(args: Array<String>) {
        val parameters = Parameter()
        val help = parameters.help(args)
        if (help) {
            showHelp()
        } else {
            producer.create(
                parameters.broker(args),
                parameters.clientId(args)
            ).use {
                val record = ProducerRecord<String, String>(
                    parameters.topic(args),
                    parameters.data(args)
                )
                parameters.headers(args).forEach(record.headers()::add)
                it.send(record)
            }
        }
    }
}

fun main(args: Array<String>) {
    ProjectMainKt(KasherProducer()).main(args)
}

fun showHelp() {
    println("""
        Options:
        $HEADER_PARAMETER  Headers: $HEADER_PARAMETER name${HEADER_PARAMETER_SEPARATOR}value $HEADER_PARAMETER name${HEADER_PARAMETER_SEPARATOR}value 
        $KAFKA_BROKER_PARAMETER  boostrap.server where you broker is located (url:port)
        $CLIENT_ID_PARAMETER  information for client that make request
        $TOPIC_PARAMETER  Topic name that want to produce/consume message
        $DATA_MESSAGE_PARAMETER  Message content. Value must have in quotation marks ("sample")
    """.trimIndent()
    )
}
