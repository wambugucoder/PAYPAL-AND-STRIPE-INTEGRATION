package com.gofundme.server

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import springfox.documentation.swagger2.annotations.EnableSwagger2

@SpringBootApplication
@EnableCaching
@EnableJpaAuditing
@EnableEncryptableProperties
class ServerApplication

fun main(args: Array<String>) {
    runApplication<ServerApplication>(*args)
}
