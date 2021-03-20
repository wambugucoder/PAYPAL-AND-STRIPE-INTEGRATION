package com.gofundme.server.controller

import com.gofundme.server.responseHandler.LogStreamResponse
import com.gofundme.server.model.UserModel
import com.gofundme.server.requestHandler.UpdateUserRequest
import com.gofundme.server.responseHandler.BlockResponse
import com.gofundme.server.responseHandler.MakeAdminResponse
import com.gofundme.server.responseHandler.UpdateUserDetailsResponse
import com.gofundme.server.service.LogStream
import com.gofundme.server.service.UserInfoService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
class AdminController {

    @Autowired
    lateinit var userInfoService: UserInfoService

    @Autowired
    lateinit var logStream: LogStream

    @GetMapping("/api/v1/admin/all-users",consumes = [MediaType.APPLICATION_JSON_VALUE],produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getAllUsers(): MutableList<UserModel> {
        return userInfoService.getAllUsers()

    }
    @GetMapping("/api/v1/admin/specific-user/{id}")
    fun getSpecificUser(@PathVariable id:Long): UserModel {
        return userInfoService.getSpecificUserInfo(id)

    }
    @PutMapping("/api/v1/admin/block-user/{email}",produces = [MediaType.APPLICATION_JSON_VALUE],consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun blockUser(@PathVariable email:String): ResponseEntity<BlockResponse> {
        return userInfoService.blockUser(email)
    }
    @PutMapping("/api/v1/admin/update-user/{id}",consumes = [MediaType.APPLICATION_JSON_VALUE],produces = [MediaType.APPLICATION_JSON_VALUE])
    fun updateUser(@PathVariable id:Long,@RequestBody @Valid updateUserRequest: UpdateUserRequest,bindingResult: BindingResult): ResponseEntity<UpdateUserDetailsResponse> {
        if (bindingResult.hasErrors()){
            logStream.sendToLogConsole(LogStreamResponse(level = "ERROR",serviceAffected = "UserInfoService",message ="${updateUserRequest.email} has invalid details in his/her update details section"))
            return ResponseEntity.badRequest().body(UpdateUserDetailsResponse(message = "Invalid Details",httpStatus = HttpStatus.BAD_REQUEST))

        }
        return userInfoService.updateUserDetails(id,updateUserRequest)

    }
    @PutMapping("/api/v1/moderator/makeAdmin/{id}")
    fun makeUserAdmin(@PathVariable id:Long): ResponseEntity<MakeAdminResponse> {
        return userInfoService.makeUserAdmin(id)

    }


}