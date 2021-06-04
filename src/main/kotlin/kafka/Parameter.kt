package kafka

import org.apache.kafka.common.header.Header
import org.apache.kafka.common.header.internals.RecordHeader

class Parameter {

    private val helpMessage = " Para mais informações utilie ${KafkaConstants.HELP_PARAMETER}"

    fun help(args: Array<String>): Boolean = args.contains(KafkaConstants.HELP_PARAMETER)

    fun broker(args: Array<String>): String =
        findParameter(args, KafkaConstants.KAFKA_BROKER_PARAMETER)
            ?: throw RuntimeException("Kafka broker deve ser informado com o parametro ${KafkaConstants.KAFKA_BROKER_PARAMETER} broker:port $helpMessage")

    fun clientId(args: Array<String>): String =
        findParameter(args, KafkaConstants.CLIENT_ID_PARAMETER)
            ?: throw RuntimeException("Client id deve ser informado com o parametro ${KafkaConstants.CLIENT_ID_PARAMETER} value $helpMessage")

    fun topic(args: Array<String>): String =
        findParameter(args, KafkaConstants.TOPIC_PARAMETER)
            ?: throw RuntimeException("Topic deve ser informado com o parametro ${KafkaConstants.TOPIC_PARAMETER} topicName $helpMessage")

    fun data(args: Array<String>): String =
        findParameter(args, KafkaConstants.DATA_MESSAGE_PARAMETER)
            ?: throw RuntimeException("Valor para a mensagem deve ser informado com o parametro ${KafkaConstants.DATA_MESSAGE_PARAMETER} value $helpMessage")

    fun headers(args: Array<String>): Iterable<Header> =
        findParameterMultipleValue(args, KafkaConstants.HEADER_PARAMETER)
            .map {
                val values = it.split(KafkaConstants.HEADER_PARAMETER_SEPARATOR)
                val key = values[0]
                val value = values[1]
                RecordHeader(key, value.toByteArray())
            }

    private fun findParameter(args: Array<String>, key: String): String? {
        for (index in args.indices) {
            val value = args[index]
            if (value.equals(key, true)) {
                return args[index + 1]
            }
        }
        return null
    }

    private fun findParameterMultipleValue(args: Array<String>, key: String): Iterable<String> {
        val positions = mutableListOf<Int>()
        for (index in args.indices) {
            val value = args[index]
            if (value.equals(key, true)) {
                positions += index + 1
            }
        }
        return args.filterIndexed { index, _ -> positions.contains(index) }
    }
}