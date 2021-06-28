package br.com.brandao

import br.com.brandao.kafka.KafkaFactory
import br.com.brandao.kafka.KasherProducer
import br.com.brandao.parameter.CommandApplication
import br.com.brandao.rest.RestApplication

interface Application {
    fun run(args: Array<String>)
}

enum class ApplicationMode {
    COMMAND_LINE, REST;
}

class ApplicationFactory {
    companion object {
        fun create(mode: ApplicationMode) : Application =
            when (mode) {
                ApplicationMode.REST -> RestApplication()
                else -> CommandApplication(
                    KasherProducer(
                        KafkaFactory(),
                        60
                    )
                )
            }
    }
}
