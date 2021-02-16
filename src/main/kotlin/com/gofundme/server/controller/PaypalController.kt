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


    @PostMapping("/api/v1/{uid}/{did}/paypal/make-pay")
    fun createPayment(@RequestBody @Valid paypalRequest: PaypalRequest,@PathVariable uid:Long,@PathVariable did:Long): ResponseEntity<PaypalResponse> {
        return paypalService.createAndRedirectToPaypal(paypalRequest,uid,did)

    }


    @GetMapping("/api/v1/paypal/pay/success")
    fun proceedToPaypalUi(@RequestParam("paymentId") paymentId: String, @RequestParam("PayerID") PayerID: String): ResponseEntity<PaypalResponse> {
        return paypalService.performTransaction(paymentId, PayerID)
    }
    @GetMapping("/api/v1/paypal/pay/cancel")
    fun cancelPayment(): ResponseEntity<PaypalResponse> {
        return paypalService.cancelTransaction()
    }
}