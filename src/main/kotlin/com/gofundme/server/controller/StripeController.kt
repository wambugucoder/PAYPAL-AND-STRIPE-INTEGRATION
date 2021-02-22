package com.gofundme.server.controller

import com.gofundme.server.service.StripeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class StripeController {

    @Autowired
    lateinit var stripeService:StripeService

    @PostMapping("/api/v1/{uid}/{did}/stripe/make-pay")
    fun paywithStripe(): String {
        return stripeService.charge()

    }

}