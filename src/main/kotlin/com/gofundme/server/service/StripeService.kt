package com.gofundme.server.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.gofundme.server.requestHandler.StripeChargeRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import com.stripe.Stripe
import com.stripe.exception.AuthenticationException
import com.stripe.exception.CardException
import com.stripe.exception.InvalidRequestException

import javax.annotation.PostConstruct
import com.stripe.model.Charge
import com.stripe.model.Token
import org.springframework.beans.factory.annotation.Autowired


@Service
class StripeService {

    @Value("\${stripe.secret.key}")
    lateinit var  secretKey:String

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @PostConstruct
    fun init() {
        Stripe.apiKey = secretKey
    }
    fun generateCreditCardToken(): String {
        val card: MutableMap<String, Any> = HashMap()
        card["number"] = "4242424242424242"
        card["exp_month"] = 2
        card["exp_year"] = 2022
        card["cvc"] = "314"
        val params: MutableMap<String, Any> = HashMap()
        params["card"] = card

        val token: Token = Token.create(params)
        return token.id
    }

    fun charge(): String {
        val chargeParams: MutableMap<String, Any> = HashMap()
        chargeParams["amount"] = 100000
        chargeParams["currency"] = "USD"
        chargeParams["description"] = "Trial"
        chargeParams["source"] = generateCreditCardToken()
         Charge.create(chargeParams)
        return "Done"
    }
}