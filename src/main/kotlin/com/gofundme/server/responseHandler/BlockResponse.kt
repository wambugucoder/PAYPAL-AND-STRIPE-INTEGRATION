package com.gofundme.server.responseHandler

import org.springframework.http.HttpStatus
import java.io.Serializable

class BlockResponse:Serializable {
    val message:String
    val httpStatus:HttpStatus


    constructor(message:String,httpStatus: HttpStatus){
        this.httpStatus=httpStatus
        this.message=message
    }
}