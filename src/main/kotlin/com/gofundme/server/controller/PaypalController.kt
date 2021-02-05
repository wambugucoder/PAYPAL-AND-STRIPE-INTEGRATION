package com.gofundme.server.controller

import com.gofundme.server.service.PaypalService
import com.paypal.base.rest.PayPalRESTException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class PaypalController {

    @Autowired
    lateinit var paypalService: PaypalService

    var CANCEL_URL="/pay/cancel"
    var SUCCESS_URL="/pay/success"


    @PostMapping("/api/v1/auth/pay")
    fun payDonation(): String {
     try {
         val payment=paypalService.createPayment(
             "7","USD","paypal","Sale","Trial", "http://localhost:8443/api/v1/auth/$CANCEL_URL",
             "https://localhost:8443/api/v1/auth/pay/success"
         )
         for (link in payment.links ){
             if (link.rel == "approval_url"){
                 return "redirect"+link.href
             }
         }
     }
     catch (e:PayPalRESTException){
         e.printStackTrace()

     }
        return "redirect:/"
    }


    @GetMapping("/api/v1/auth/pay/success")
    fun proceedToPaypalUi(@RequestParam("paymentId")paymentId:String,
                          @RequestParam("PayerID")PayerID:String): String {
        try {
            val payment=paypalService.executePayment(paymentId,PayerID)
            if (payment.state == "approved"){
                return "success"
            }
        }
        catch (e:PayPalRESTException){
            e.printStackTrace()

        }
        return "rediret:/"
    }
}