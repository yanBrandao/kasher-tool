package br.com.brandao.rest

import br.com.brandao.Application
import io.micronaut.runtime.Micronaut

class RestApplication : Application {
    override fun run(args: Array<String>) {
        Micronaut.build()
            .args(*args)
            .packages("br.com.brandao")
            .start()
    }
}
