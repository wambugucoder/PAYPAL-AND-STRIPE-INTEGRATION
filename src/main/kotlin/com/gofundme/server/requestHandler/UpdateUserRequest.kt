package com.gofundme.server.requestHandler

import java.io.Serializable
import javax.validation.constraints.Email
import javax.validation.constraints.NotEmpty

class UpdateUserRequest (
    @field:NotEmpty(message = "UserName cannot Be Empty")
    var username:String,

    @field:NotEmpty(message = "Email cannot Be Empty")
    @field:Email(message = "Invalid Email")
    var email:String
        ){
    constructor():this("","")

}