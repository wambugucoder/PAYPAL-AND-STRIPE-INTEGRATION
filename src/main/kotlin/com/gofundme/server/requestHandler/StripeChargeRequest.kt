package com.gofundme.server.requestHandler

class StripeChargeRequest {

    var currency:String="USD"
    var description: String
    var amount :Int
    var stripeEmail: String
    var stripeToken: String

    constructor(description: String,amount :Int,stripeEmail: String,stripeToken: String){
        this.amount=amount
        this.currency=currency
        this.description=description
        this.stripeEmail=stripeEmail
        this.stripeToken=stripeToken


    }
}