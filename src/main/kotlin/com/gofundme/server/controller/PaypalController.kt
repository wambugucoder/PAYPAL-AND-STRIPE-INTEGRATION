package com.gofundme.server.controller

import com.gofundme.server.requestHandler.PaypalRequest
import com.gofundme.server.responseHandler.PaypalResponse
import com.gofundme.server.service.PaypalService
import com.paypal.base.rest.PayPalRESTException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
class PaypalController {

    @Autowired
    lateinit var paypalService: PaypalService


    @PostMapping("/api/v1/auth/pay")
    fun createPayment(@RequestBody @Valid paypalRequest: PaypalRequest): ResponseEntity<PaypalResponse> {
        return paypalService.createAndRedirectToPaypal(paypalRequest)

    }


    @GetMapping("/api/v1/auth/pay/success")
    fun proceedToPaypalUi(@RequestParam("paymentId") paymentId: String, @RequestParam("PayerID") PayerID: String): ResponseEntity<PaypalResponse> {
        return paypalService.performTransaction(paymentId, PayerID)
    }
    @GetMapping("/api/v1/auth/pay/cancel")
    fun cancelPayment(): ResponseEntity<PaypalResponse> {
        return paypalService.cancelTransaction()
    }
}