package com.gofundme.server.requestHandler

import com.gofundme.server.model.UserModel
import javax.validation.constraints.NotEmpty

class DonationHandler{
    @field:NotEmpty(message = "Details Should Not Be Empty")
    var details:String

    @field:NotEmpty(message = "Category Should Not Be Empty")
    var category:String

    @field:NotEmpty(message = "Target Should Not Be Empty")
    var target:String





    constructor(details: String,category: String,target: String){
        this.details=details
        this.category=category
        this.target=target

    }
}