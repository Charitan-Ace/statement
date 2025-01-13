package com.charitan.statement

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

@SpringBootApplication
@EnableDiscoveryClient
class StatementApplication

fun main(args: Array<String>) {
    runApplication<StatementApplication>(*args)
}
