package com.gofundme.server.responseHandler

import java.io.Serializable
import java.time.LocalDateTime

class LogStreamResponse:Serializable {

    var time:LocalDateTime= LocalDateTime.now()

    var level:String

    var serviceAffected:String

    var message:String

    constructor(level:String,serviceAffected:String,message:String){
        this.time=time
        this.level=level.toUpperCase()
        this.serviceAffected=serviceAffected
        this.message=message
    }
}