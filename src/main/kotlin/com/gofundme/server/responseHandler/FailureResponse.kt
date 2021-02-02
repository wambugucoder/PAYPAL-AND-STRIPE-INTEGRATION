package com.gofundme.server.responseHandler

import org.springframework.http.HttpStatus
import java.io.Serializable

class FailureResponse : Serializable {
    val message:String

    val httpStatus: HttpStatus

    constructor(message:String,httpStatus: HttpStatus){
        this.message=message
        this.httpStatus=httpStatus
    }
}