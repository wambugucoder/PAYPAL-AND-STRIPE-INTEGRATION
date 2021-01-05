package com.gofundme.server.requestHandler

import javax.validation.constraints.Email
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.Size

class LoginHandler(

    @field:NotEmpty(message = "Email Cannot Be Empty")
    @field:Email(message = "Incorrect Email")
    val email:String,

    @field:NotEmpty(message = "Password Cannot Be Empty")
    val password:String
) {
    constructor():this("","")
}