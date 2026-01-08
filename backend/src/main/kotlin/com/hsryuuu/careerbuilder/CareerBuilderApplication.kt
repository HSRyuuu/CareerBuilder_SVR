package com.hsryuuu.careerbuilder

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class CareerBuilderApplication

fun main(args: Array<String>) {
    runApplication<CareerBuilderApplication>(*args)
}
