package com.gofundme.server.service

import LogStreamResponse
import com.fasterxml.jackson.databind.ObjectMapper
import com.gofundme.server.requestHandler.StripeChargeRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import com.stripe.Stripe
import com.stripe.exception.AuthenticationException
import com.stripe.exception.CardException
import com.stripe.exception.InvalidRequestException
import com.stripe.exception.StripeException

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

    @Autowired
    lateinit var donationsService: DonationsService

    @Autowired
    lateinit var userInfoService: UserInfoService

    @Autowired
    lateinit var logStream: LogStream

    @PostConstruct
    fun init() {
        Stripe.apiKey = secretKey
    }
    fun generateCreditCardToken(stripeChargeRequest: StripeChargeRequest): String {
        val card: MutableMap<String, Any> = HashMap()
        card["number"] =stripeChargeRequest.cardNumber
        card["exp_month"] = stripeChargeRequest.exp_month
        card["exp_year"] = stripeChargeRequest.exp_year
        card["cvc"] = stripeChargeRequest.cvc
        val params: MutableMap<String, Any> = HashMap()
        params["card"] = card

        val token: Token = Token.create(params)
        return token.id
    }

    fun charge(stripeChargeRequest: StripeChargeRequest,uid:Long,did:Long): String {
        val donationDetails=donationsService.getSpecificDonationsById(did)
        val userDetails=userInfoService.getSpecificUserInfo(uid)
        val chargeParams: MutableMap<String, Any> = HashMap()
        chargeParams["amount"] = stripeChargeRequest.amount
        chargeParams["currency"] = "USD"
        chargeParams["description"] = donationDetails.details
        chargeParams["source"] = generateCreditCardToken(stripeChargeRequest)
        try {
            val stripeDetails=Charge.create(chargeParams)
        }
        catch (e:StripeException){
            logStream.sendToLogConsole(LogStreamResponse(level = "ERROR",serviceAffected = "StripeService",message = "$userDetails."))

        }

        return "Done"
    }
}