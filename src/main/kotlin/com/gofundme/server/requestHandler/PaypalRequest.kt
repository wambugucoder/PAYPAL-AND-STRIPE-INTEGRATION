package com.gofundme.server.requestHandler

import javax.validation.constraints.NotEmpty

class PaypalRequest{
    @field:NotEmpty(message = "Total Cannot Be Empty")
    var total:String


    constructor(total:String){
        this.total=total

    }
}



