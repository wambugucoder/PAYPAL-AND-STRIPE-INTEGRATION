package com.gofundme.server


import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Profile
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.scheduling.annotation.EnableScheduling


@SpringBootApplication
@EnableCaching
@EnableJpaAuditing
@EnableEncryptableProperties
@EnableScheduling
class ServerApplication


fun main(args: Array<String>) {
    runApplication<ServerApplication>(*args)
}
