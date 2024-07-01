package com.riteny

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ApplicationCoreApplication

fun main(args: Array<String>) {
    runApplication<ApplicationCoreApplication>(*args)
}

