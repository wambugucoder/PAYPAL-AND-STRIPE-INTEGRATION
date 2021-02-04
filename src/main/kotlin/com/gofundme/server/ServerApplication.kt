package com.gofundme.server

import com.gofundme.server.service.EmailService
import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.scheduling.annotation.EnableScheduling
import springfox.documentation.swagger2.annotations.EnableSwagger2

@SpringBootApplication
@EnableCaching
@EnableJpaAuditing
@EnableEncryptableProperties
@EnableScheduling
class ServerApplication


fun main(args: Array<String>) {
    runApplication<ServerApplication>(*args)
}
