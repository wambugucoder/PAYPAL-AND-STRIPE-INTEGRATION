package com.gofundme.server.service

import com.gofundme.server.requestHandler.StripeChargeRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import com.stripe.Stripe
import com.stripe.exception.AuthenticationException
import com.stripe.exception.CardException
import com.stripe.exception.InvalidRequestException

import javax.annotation.PostConstruct
import com.stripe.model.Charge







@Service
class StripeService {
    @Value("\${stripe.secret.key}")
    lateinit var  secretKey:String

    @PostConstruct
    fun init() {
        Stripe.apiKey = secretKey
    }


    fun charge(chargeRequest: StripeChargeRequest): Charge? {
        val chargeParams: MutableMap<String, Any> = HashMap()
        chargeParams["amount"] = chargeRequest.amount
        chargeParams["currency"] = chargeRequest.currency
        chargeParams["description"] = chargeRequest.description
        chargeParams["source"] = chargeRequest.stripeToken
        return Charge.create(chargeParams)
    }
}