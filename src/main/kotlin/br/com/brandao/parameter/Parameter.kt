package br.com.brandao.parameter

import br.com.brandao.parameter.KafkaConstants.Companion.CLIENT_ID_PARAMETER
import br.com.brandao.parameter.KafkaConstants.Companion.DATA_MESSAGE_PARAMETER
import br.com.brandao.parameter.KafkaConstants.Companion.HEADER_PARAMETER
import br.com.brandao.parameter.KafkaConstants.Companion.HEADER_PARAMETER_SEPARATOR
import br.com.brandao.parameter.KafkaConstants.Companion.HELP_PARAMETER
import br.com.brandao.parameter.KafkaConstants.Companion.KAFKA_BROKER_PARAMETER
import br.com.brandao.parameter.KafkaConstants.Companion.TOPIC_PARAMETER
import org.apache.kafka.common.header.Header
import org.apache.kafka.common.header.internals.RecordHeader
import java.util.*

class Parameter {

    private val helpMessage = " For more information use $HELP_PARAMETER"

    fun help(args: Array<String>): Boolean = args.contains(HELP_PARAMETER)

    fun broker(args: Array<String>): String =
        findParameter(args, KAFKA_BROKER_PARAMETER)
            ?: throw RuntimeException("bootstrap server must be informed with param $KAFKA_BROKER_PARAMETER broker:port $helpMessage")

    fun clientId(args: Array<String>): String? =
        findParameter(args, CLIENT_ID_PARAMETER)

    fun topic(args: Array<String>): String =
        findParameter(args, TOPIC_PARAMETER)
            ?: throw RuntimeException("Topic must be informed with param $TOPIC_PARAMETER topicName $helpMessage")

    fun data(args: Array<String>): String =
        findParameter(args, DATA_MESSAGE_PARAMETER)
            ?: throw RuntimeException("Message must be informed with param $DATA_MESSAGE_PARAMETER value $helpMessage")

    fun headers(args: Array<String>): Iterable<Header> {
        return findParameterMultipleHeaderValue(args)
            .map {
                val values = it.split(HEADER_PARAMETER_SEPARATOR)
                val key = values[0]
                val value = values[1]
                RecordHeader(key, value.toByteArray())
            }
    }

    private fun findParameter(args: Array<String>, key: String): String? {
        for (index in args.indices) {
            val value = args[index]
            if (value.equals(key, true)) {
                return getSafeValue(args, index + 1)
                    ?: throw RuntimeException("Could not found value for param $key")
            }
        }
        return null
    }

    private fun findParameterMultipleHeaderValue(args: Array<String>): Iterable<String> {
        val positions = mutableListOf<Int>()
        for (index in args.indices) {
            val value = args[index]
            if (value.equals(HEADER_PARAMETER, true)) {
                positions += index + 1
            }
        }
        return positions.map {
            getSafeValue(args, it)
                ?: throw RuntimeException("Could not found value for param $HEADER_PARAMETER")
        }
    }

    private fun getSafeValue(args: Array<String>, position: Int) =
        if (position > -1 && position < args.size) sanityCheckValue(args[position]) else null

    private fun sanityCheckValue(value: String) =
        if (value.startsWith("-", true)) null else value

}