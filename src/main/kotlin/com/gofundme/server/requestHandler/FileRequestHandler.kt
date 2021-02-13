package com.gofundme.server.requestHandler

import javax.validation.constraints.Email
import javax.validation.constraints.NotEmpty

class FileRequestHandler {
    @field:NotEmpty(message = "FileName Cannot Be Empty")
    var name:String?

    @field:NotEmpty(message = "FileType Cannot Be Empty")
    var type:String?

    @field:NotEmpty(message = "FileData Cannot Be Empty")
    var data:ByteArray

    constructor(name:String,type:String,data:ByteArray){
        this.name=name
        this.type=type
        this.data=data
    }
}