package com.gofundme.server.requestHandler

import com.gofundme.server.model.UserModel
import org.springframework.web.multipart.MultipartFile
import javax.validation.constraints.NotEmpty

class DonationHandler{
    @field:NotEmpty(message = "Details Should Not Be Empty")
    var details:String

    @field:NotEmpty(message = "Category Should Not Be Empty")
    var category:String

    @field:NotEmpty(message = "Target Should Not Be Empty")
    var target:String

    @field:NotEmpty(message = "File Should Not Be Empty")
    var file:MultipartFile





    constructor(details: String,category: String,target: String,file:MultipartFile){
        this.details=details
        this.category=category
        this.target=target
        this.file=file

    }
}