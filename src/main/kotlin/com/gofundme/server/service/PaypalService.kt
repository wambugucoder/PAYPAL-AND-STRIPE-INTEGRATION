package com.gofundme.server.service

import LogStreamResponse
import com.gofundme.server.requestHandler.PaypalRequest
import com.gofundme.server.responseHandler.PaypalResponse
import com.paypal.api.payments.*
import com.paypal.base.rest.APIContext
import com.paypal.base.rest.PayPalRESTException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RequestParam
import javax.servlet.http.HttpServletResponse


@Service
class PaypalService {

    @Autowired
    lateinit var apiContext: APIContext

    @Autowired
    lateinit var logStream: LogStream



    fun createPayment(total:String, currency:String, method:String, intent:String, description:String, cancelUrl:String, successUrl:String):Payment {
        //CONFIGURE AMOUNT AND CURRENCY
        val amount = Amount()
        amount.currency = currency
        amount.total = total

        //CREATE A TRANSACTION
        val transaction:Transaction=Transaction()
        transaction.description=description
        transaction.amount=amount

        val transactions = ArrayList<Transaction>()
        transactions.add(transaction)

        //LINK THE PAYER
        val payer= Payer()
        payer.paymentMethod=method.toString()

        //PAYMENT OBJECT
        val payment= Payment()
        payment.intent=intent.toString()
        payment.payer=payer
        payment.transactions=transactions

        //REDIRECT URLS
        val redirectUrls = RedirectUrls()
        redirectUrls.cancelUrl=cancelUrl
        redirectUrls.returnUrl=successUrl
        payment.redirectUrls=redirectUrls

        logStream.sendToLogConsole(LogStreamResponse(level = "INFO",serviceAffected = "PaypalService",message = "Paypal Payment Created"))

        return payment.create(apiContext)

    }
    fun executePayment(paymentId:String,payerId:String):Payment{
        val payment=Payment()
        payment.id=paymentId
        val paymentExecution =PaymentExecution()
        paymentExecution.payerId=payerId

        return payment.execute(apiContext,paymentExecution)
    }
    fun createAndRedirectToPaypal(paypalRequest:PaypalRequest): ResponseEntity<PaypalResponse> {
        try {
            val payment=createPayment(
                total=paypalRequest.total,
                currency = "USD",
                method = "paypal",
                intent = paypalRequest.intent,
                description = paypalRequest.description,
                cancelUrl = "http://localhost:8443/api/v1/auth/pay/cancel",
                successUrl ="https://localhost:8443/api/v1/auth/pay/success"
            )

            for (link in payment.links ){
                if (link.rel == "approval_url"){

                    logStream.sendToLogConsole(LogStreamResponse(level = "INFO",serviceAffected = "Paypal",message = "Redirecting User to Paypal"))
                    return ResponseEntity.ok().body(PaypalResponse(message =link.href ,httpStatus = HttpStatus.OK))
                }
            }
        }
        catch (e: PayPalRESTException){
            logStream.sendToLogConsole(LogStreamResponse(level = "ERROR",serviceAffected = "Paypal",message = e.toString()))
            return ResponseEntity.badRequest().body(PaypalResponse(message = "Oops,Error Creating Payment",httpStatus = HttpStatus.BAD_REQUEST))

        }
        logStream.sendToLogConsole(LogStreamResponse(level = "WARN",serviceAffected = "Paypal",message = "Something is Wrong with Users input"))
        return ResponseEntity.badRequest().body(PaypalResponse(message = "Oops,Error Creating Payment",httpStatus = HttpStatus.BAD_REQUEST))
    }
    fun performTransaction(paymentId:String,PayerID:String): ResponseEntity<PaypalResponse> {
        try {
            val payment=executePayment(paymentId,PayerID)
            if (payment.state == "approved"){
                logStream.sendToLogConsole(LogStreamResponse(level = "SUCCESS",serviceAffected = "Paypal",message =payment.toString()))
                //SAVE TRANSACTION DETAILS
                return ResponseEntity.ok().body(PaypalResponse(message = "Your Payment Has Been Received",httpStatus = HttpStatus.OK))

            }
        }
        catch (e:PayPalRESTException){
            logStream.sendToLogConsole(LogStreamResponse(level = "ERROR",serviceAffected = "Paypal",message = e.toString()))
            return ResponseEntity.badRequest().body(PaypalResponse(message = "Oops,Error Executing Payment",httpStatus = HttpStatus.BAD_REQUEST))

        }
        logStream.sendToLogConsole(LogStreamResponse(level = "ERROR",serviceAffected = "Paypal",message = "Executing Paypal transaction for payerid-$PayerID failed"))
        return ResponseEntity.badRequest().body(PaypalResponse(message = "Oops,Error Executing Payment",httpStatus = HttpStatus.BAD_REQUEST))
    }
    fun cancelTransaction(): ResponseEntity<PaypalResponse> {
        logStream.sendToLogConsole(LogStreamResponse(level = "INFO",serviceAffected = "Paypal",message = "A Paypal Transaction has been cancelled"))
        return ResponseEntity.badRequest().body(PaypalResponse(message = "Your Transaction Has Been Cancelled",httpStatus = HttpStatus.BAD_REQUEST))
    }
}