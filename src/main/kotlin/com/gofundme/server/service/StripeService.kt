package com.gofundme.server.service

import LogStreamResponse
import com.fasterxml.jackson.databind.ObjectMapper
import com.gofundme.server.model.TransactionsModel
import com.gofundme.server.repository.TransactionRepository
import com.gofundme.server.requestHandler.StripeChargeRequest
import com.gofundme.server.responseHandler.StripeResponse
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
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity


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

    @Autowired
    lateinit var transactionRepository: TransactionRepository

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

    fun charge(stripeChargeRequest: StripeChargeRequest,uid:Long,did:Long): ResponseEntity<StripeResponse> {
        val donationDetails=donationsService.getSpecificDonationsById(did)
        val userDetails=userInfoService.getSpecificUserInfo(uid)
        val chargeParams: MutableMap<String, Any> = HashMap()
        chargeParams["amount"] = stripeChargeRequest.amount
        chargeParams["currency"] = "USD"
        chargeParams["description"] = donationDetails.details
        chargeParams["source"] = generateCreditCardToken(stripeChargeRequest)
        try {
            val stripeDetails=Charge.create(chargeParams)
            // SAVE TRANSACTIONS
            val transactionDetails=TransactionsModel(
                initiator = userDetails,
                amountDonated = stripeChargeRequest.amount.toDouble(),
                donation = donationDetails,
                receiptId = stripeDetails.receiptNumber
            )
            transactionRepository.save(transactionDetails)
            //UPDATE DONATION COUNT AND USER WHO DONATED
            donationsService.updateDonationCountAndList(did,transactionDetails)
            logStream.sendToLogConsole(LogStreamResponse(level = "SUCCESS",serviceAffected = "StripeService",message ="${userDetails.email} just donated ${stripeChargeRequest.amount} via stripe"))
        }
        catch (e:StripeException){
            logStream.sendToLogConsole(LogStreamResponse(level = "ERROR",serviceAffected = "StripeService",message = "${userDetails.email} has the following errors: ${e.stripeError}"))
            return ResponseEntity.badRequest().body(StripeResponse("Incorrect card Details",httpStatus = HttpStatus.BAD_REQUEST))
        }


        return ResponseEntity.ok().body(StripeResponse("Thank you for you Donation",httpStatus = HttpStatus.OK))
    }
}