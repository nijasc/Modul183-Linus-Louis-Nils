package lol.linkstack

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
@EnableJpaAuditing
class LinkstackApplication

fun main(args: Array<String>) {
    runApplication<LinkstackApplication>(*args)
}
