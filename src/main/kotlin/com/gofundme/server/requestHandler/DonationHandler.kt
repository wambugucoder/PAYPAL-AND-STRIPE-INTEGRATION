package com.gofundme.server.requestHandler

import com.gofundme.server.model.UserModel
import javax.validation.constraints.NotEmpty

class DonationHandle {
    @field:NotEmpty(message = "Details Should Not Be Empty")
    var details:String

    @field:NotEmpty(message = "Category Should Not Be Empty")
    var category:String

    @field:NotEmpty(message = "Target Should Not Be Empty")
    var target:Int

    @field:NotEmpty(message = "CreatedBy Should Not Be Empty")
    var createdBy:UserModel



    constructor(details: String,category: String,target: Int,createdBy:UserModel){
        this.details=details
        this.category=category
        this.target=target
        this.createdBy=createdBy


    }
}