package com.gofundme.server.service

import com.paypal.api.payments.*
import com.paypal.base.rest.APIContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode


@Service
class PaypalService {

    @Autowired
    lateinit var apiContext: APIContext

    fun createPayment(
        total:String,
        currency:String,
        method:String,
        intent:String,
        description:String,
        cancelUrl:String,
        successUrl:String
    ):Payment {
        //CONFIGURE AMOUNT AND CURRENCY
        val amount = Amount()
        amount.currency = currency
        //val total = BigDecimal(total).setScale(2, RoundingMode.HALF_UP).toDouble()
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

        //PAYEMENT OBJECT
        val payment= Payment()
        payment.intent=intent.toString()
        payment.payer=payer
        payment.transactions=transactions

        //REDIRECT URLS
        val redirectUrls = RedirectUrls()
        redirectUrls.cancelUrl=cancelUrl
        redirectUrls.returnUrl=successUrl
        payment.redirectUrls=redirectUrls

        return payment.create(apiContext)

    }
    fun executePayment(paymentId:String,payerId:String):Payment{
        val payment=Payment()
        payment.id=paymentId
        val paymentExecution =PaymentExecution()
        paymentExecution.payerId=payerId

        return payment.execute(apiContext,paymentExecution)
    }
}