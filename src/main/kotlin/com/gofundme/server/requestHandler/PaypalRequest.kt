package com.gofundme.server.requestHandler

import javax.validation.constraints.NotEmpty

class PaypalRequest{
    @field:NotEmpty(message = "Total Cannot Be Empty")
    var total:String

    @field:NotEmpty(message = "Description Cannot Be Empty")
    var description:String

    @field:NotEmpty(message = "Intent Cannot Be Empty")
    var intent:String

    constructor(total:String,description:String,intent:String){
        this.total=total
        this.description=description
        this.intent=intent
    }
}



