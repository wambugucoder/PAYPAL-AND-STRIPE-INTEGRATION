package com.gofundme.server.controller

import com.gofundme.server.requestHandler.StripeChargeRequest
import com.gofundme.server.responseHandler.StripeResponse
import com.gofundme.server.service.StripeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class StripeController {

    @Autowired
    lateinit var stripeService:StripeService

    @PostMapping("/api/v1/{uid}/{did}/stripe/make-pay")
    fun paywithStripe(@RequestBody stripeChargeRequest:StripeChargeRequest,@PathVariable uid:Long,@PathVariable did:Long): ResponseEntity<StripeResponse> {
        return stripeService.charge(stripeChargeRequest,uid,did)

    }

}