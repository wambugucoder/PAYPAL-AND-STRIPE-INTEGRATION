package com.gofundme.server.service

import LogStreamResponse
import com.gofundme.server.model.TransactionsModel
import com.gofundme.server.repository.DonationsRepository
import com.gofundme.server.repository.TransactionRepository
import com.gofundme.server.repository.UserRepository
import com.gofundme.server.requestHandler.PaypalRequest
import com.gofundme.server.responseHandler.PaypalResponse
import com.paypal.api.payments.*
import com.paypal.base.rest.APIContext
import com.paypal.base.rest.PayPalRESTException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service


@Service
class PaypalService {

    @Autowired
    lateinit var apiContext: APIContext

    @Autowired
    lateinit var logStream: LogStream

    @Autowired
    lateinit var donationsRepository:DonationsRepository

    @Autowired
    lateinit var transactionRepository: TransactionRepository

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var donationsService: DonationsService


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
    fun createAndRedirectToPaypal(paypalRequest:PaypalRequest,uid:Long,did:Long): ResponseEntity<PaypalResponse> {
        val donationDetails=donationsRepository.findSpecificDonationById(did)

        try {
            val payment=createPayment(
                total=paypalRequest.total,
                currency = "USD",
                method = "paypal",
                intent = donationDetails.category,
                description = donationDetails.details,
                cancelUrl = "http://localhost:8443/api/v1/paypal-payment/$uid/$did/cancel",
                successUrl ="https://localhost:8443/api/v1/paypal-payment/$uid/$did/success"
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
    fun performTransaction(paymentId:String,PayerID:String,uid:Long,did:Long): ResponseEntity<PaypalResponse> {
        val userDetails= userRepository.findUserById(uid)
        val donationDetails=donationsRepository.findSpecificDonationById(did)


        try {
            val payment=executePayment(paymentId,PayerID)
            if (payment.state == "approved"){

                val transactionDetails=TransactionsModel(
                    initiator = userDetails,
                    donation = donationDetails,
                    amountDonated = payment.transactions[0].amount.total.toDouble(),
                    receiptId = payment.id.toString()
                )
                //SAVE TRANSACTION
                transactionRepository.save(transactionDetails)
                //UPDATE DONATION COUNT AND USER WHO DONATED
                donationsService.updateDonationCountAndList(did,transactionDetails)
                logStream.sendToLogConsole(LogStreamResponse(level = "SUCCESS",serviceAffected = "Paypal",message ="${userDetails.email} just donated via paypal"))

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
    fun cancelTransaction(uid:Long): ResponseEntity<PaypalResponse> {
        val userDetails= userRepository.findUserById(uid)
        logStream.sendToLogConsole(LogStreamResponse(level = "INFO",serviceAffected = "Paypal",message = "${userDetails.email} just cancelled a paypal transaction"))
        return ResponseEntity.badRequest().body(PaypalResponse(message = "Your Transaction Has Been Cancelled",httpStatus = HttpStatus.BAD_REQUEST))
    }
}