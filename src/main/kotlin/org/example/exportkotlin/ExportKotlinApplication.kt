package org.example.exportkotlin

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@EnableJpaAuditing
@SpringBootApplication
class ExportKotlinApplication

fun main(args: Array<String>) {
    runApplication<ExportKotlinApplication>(*args)
}
