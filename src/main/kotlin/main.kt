import kafka.KafkaConstants
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
    println(
        """
        Options:
        ${KafkaConstants.HEADER_PARAMETER}  Headers: ${KafkaConstants.HEADER_PARAMETER} name${KafkaConstants.HEADER_PARAMETER_SEPARATOR}value ${KafkaConstants.HEADER_PARAMETER} name${KafkaConstants.HEADER_PARAMETER_SEPARATOR}value 
        ${KafkaConstants.KAFKA_BROKER_PARAMETER}  broker:port
        ${KafkaConstants.CLIENT_ID_PARAMETER}  ClientId
        ${KafkaConstants.TOPIC_PARAMETER}  Topic name
        ${KafkaConstants.DATA_MESSAGE_PARAMETER}  Conteúdo da mensagem. Não deve conter espaços
    """.trimIndent()
    )
}
