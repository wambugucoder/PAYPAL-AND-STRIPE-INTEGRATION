package com.gofundme.server.responseHandler

import org.springframework.http.HttpStatus
import java.io.Serializable

class LoginResponse: Serializable {
    val message:String

    val httpStatus: HttpStatus

    val token: String?

    constructor(message:String, httpStatus: HttpStatus, token: String?){
        this.message=message
        this.httpStatus=httpStatus
        this.token=token
    }
}