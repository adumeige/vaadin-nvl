package org.antoined.vaadin.nvl.demo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/** Spring Boot application entry point for the NVL component demo. */
@SpringBootApplication
class Application

/** Launches the demo application. */
fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
