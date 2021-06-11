package br.com.brandao

import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val modeAsString = System.getProperty("MODE", ApplicationMode.COMMAND_LINE.name)
    var mode: ApplicationMode? = null
    try {
        mode = ApplicationMode.valueOf(modeAsString)
    } catch (e: IllegalArgumentException) {
        print("Invalid type application. Validates types are: ${ApplicationMode.COMMAND_LINE} or ${ApplicationMode.REST}")
        exitProcess(0)
    }
    ApplicationFactory.create(mode!!)
        .run(args)
}
