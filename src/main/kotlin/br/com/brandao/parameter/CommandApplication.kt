package br.com.brandao.parameter

import br.com.brandao.Application
import br.com.brandao.kafka.KafkaMessage
import br.com.brandao.kafka.KafkaProperties
import br.com.brandao.kafka.KasherProducer

class CommandApplication(
    private val producer: KasherProducer
) : Application {

    override fun run(args: Array<String>) {
        val parameters = Parameter()
        val help = parameters.help(args)
        if (help) {
            showHelp()
        } else {
            val properties = KafkaProperties(
                kafkaBroker = parameters.broker(args),
                clientId = parameters.clientId(args)
            )
            val message = KafkaMessage(
                topic = parameters.topic(args),
                data = parameters.data(args),
                headers = parameters.headers(args)
            )
            producer.publish(properties, message)
        }
    }

    private fun showHelp() {
        println(
            """Options:
        ${KafkaConstants.HEADER_PARAMETER}  Headers: ${KafkaConstants.HEADER_PARAMETER} name${KafkaConstants.HEADER_PARAMETER_SEPARATOR}value ${KafkaConstants.HEADER_PARAMETER} name${KafkaConstants.HEADER_PARAMETER_SEPARATOR}value 
        ${KafkaConstants.KAFKA_BROKER_PARAMETER}  boostrap.server where you broker is located (url:port)
        ${KafkaConstants.CLIENT_ID_PARAMETER}  information for client that make request
        ${KafkaConstants.TOPIC_PARAMETER}  Topic name that want to produce/consume message
        ${KafkaConstants.DATA_MESSAGE_PARAMETER}  Message content. Value must have in quotation marks ("sample")
    """.trimIndent()
        )
    }

}
