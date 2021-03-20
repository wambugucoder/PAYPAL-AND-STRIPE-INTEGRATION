package com.gofundme.server.responseHandler

import java.io.Serializable
import java.time.LocalDateTime

class LogStreamResponse:Serializable{
    var time:LocalDateTime= LocalDateTime.now()
    val level:String
    val serviceAffected:String
    val message:String

    constructor(level:String,serviceAffected:String,message:String){
        this.level=level
        this.serviceAffected=serviceAffected
        this.message=message
        this.time=time

    }

}
