package com.gofundme.server.requestHandler

import javax.validation.constraints.Email
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.Size

class RegisterHandler(
    @field:NotEmpty(message = "UserName Cannot Be Empty")
    @field:Size(min = 6,max = 12,message = "UserName must contain 6-12 characters")
    var username:String,

    @field:NotEmpty(message = "Email Cannot Be Empty")
    @field:Email(message = "Incorrect Email")
    var email:String,

    @field:NotEmpty(message = "Password Cannot Be Empty")
    @field:Size(min = 6,message = "Password Must contain atleast 6 characters")
    var password:String,

    @field:NotEmpty(message = "Country Cannot Be Empty")
    var country:String,

    @field:NotEmpty(message = "City Cannot Be Empty")
    var city:String,

){
    constructor():this("","","","","")
}