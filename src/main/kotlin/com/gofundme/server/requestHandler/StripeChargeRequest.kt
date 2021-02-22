package com.gofundme.server.requestHandler

class StripeChargeRequest {

    var amount :Int
    var cardNumber: String
    var exp_month:Int
    var exp_year:Int
    var cvc:String

    constructor(amount :Int,cardNumber: String,exp_month:Int,exp_year:Int, cvc:String){
        this.amount=amount
        this.cardNumber=cardNumber
        this.exp_month=exp_month
        this.exp_year=exp_year
        this.cvc=cvc


    }
}
