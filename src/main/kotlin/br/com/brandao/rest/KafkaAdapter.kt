package br.com.brandao.rest

import br.com.brandao.kafka.KafkaMessage
import br.com.brandao.kafka.KafkaProperties
import io.micronaut.core.annotation.Introspected
import org.apache.kafka.common.header.internals.RecordHeader
import org.apache.kafka.common.serialization.ByteArraySerializer
import org.apache.kafka.common.serialization.Serializer
import org.apache.kafka.common.serialization.StringSerializer
import javax.validation.Valid
import javax.validation.constraints.NotNull
import kotlin.reflect.KClass

@Introspected
data class KafkaAdapter(
    @field:NotNull
    val broker: String? = null,
    val clientId: String? = null,
    @field:NotNull
    val data: String? = null,
    @field:Valid
    val headers: Iterable<Header>? = null,
    val keySerializer: String? = null,
    val valueSerializer: String? = null
) {

    fun toKafkaProperties(): KafkaProperties {
        return KafkaProperties(
            kafkaBroker = broker!!,
            clientId = clientId,
            keySerializer = keySerializer?.let { Class.forName(it).kotlin as KClass<ByteArraySerializer> },
            valueSerializer = valueSerializer?.let { Class.forName(it).kotlin as KClass<ByteArraySerializer> },
        )
    }

    fun toMessage(topic: String): KafkaMessage {
        return KafkaMessage(
            topic = topic,
            data = data!!,
            headers = headers?.map {
                RecordHeader(it.name!!, it.value!!.toByteArray())
            }
        )
    }

}

@Introspected
data class Header(
    @field:NotNull
    val name: String? = null,
    @field:NotNull
    val value: String? = null
)
