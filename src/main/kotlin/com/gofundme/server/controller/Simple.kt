package com.gofundme.server.controller

import com.gofundme.server.service.EmailService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class Simple {

    @Autowired
    lateinit var emailService: EmailService

    @GetMapping("/api/v1/auth/hello")
    fun hello() {
        return emailService.sendAccountActivationEmail("werr")
    }
}