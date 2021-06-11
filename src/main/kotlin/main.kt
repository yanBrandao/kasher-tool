import kafka.KafkaConstants
import kafka.KafkaConstants.Companion.HEADER_PARAMETER
import kafka.KafkaConstants.Companion.HEADER_PARAMETER_SEPARATOR
import kafka.KasherProducer
import kafka.Parameter
import org.apache.kafka.clients.producer.ProducerRecord
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val parameters = Parameter()
    val help = parameters.help(args)
    if (help) {
        showHelp()
        exitProcess(0)
    }

    KasherProducer.create(
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

fun showHelp() {
    println("""
        Options:
        $HEADER_PARAMETER  Headers: $HEADER_PARAMETER name${HEADER_PARAMETER_SEPARATOR}value $HEADER_PARAMETER name${HEADER_PARAMETER_SEPARATOR}value 
        ${KafkaConstants.KAFKA_BROKER_PARAMETER}  boostrap.server where you broker is located (url:port)
        ${KafkaConstants.CLIENT_ID_PARAMETER}  information for client that make request
        ${KafkaConstants.TOPIC_PARAMETER}  Topic name that want to produce/consume message
        ${KafkaConstants.DATA_MESSAGE_PARAMETER}  Message content. Value must have in quotation marks ("sample")
    """.trimIndent()
    )
}
