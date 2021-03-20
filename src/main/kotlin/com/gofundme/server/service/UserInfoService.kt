package com.gofundme.server.service

import com.gofundme.server.responseHandler.LogStreamResponse
import com.gofundme.server.model.UserModel
import com.gofundme.server.model.UserRoles
import com.gofundme.server.repository.UserRepository
import com.gofundme.server.requestHandler.UpdateUserRequest
import com.gofundme.server.responseHandler.BlockResponse
import com.gofundme.server.responseHandler.MakeAdminResponse
import com.gofundme.server.responseHandler.UpdateUserDetailsResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class UserInfoService {

    @Autowired
    lateinit var logStream: LogStream
    @Autowired
    lateinit var userRepository: UserRepository

    fun getAllUsers(): MutableList<UserModel> {
        return userRepository.findAll()

    }
    fun getSpecificUserInfo(id:Long): UserModel {
       if(!userRepository.existsById(id)){
           logStream.sendToLogConsole(LogStreamResponse(level = "WARN",serviceAffected = "UserInfoService",message = "No UserId ${id}"))

       }
        logStream.sendToLogConsole(LogStreamResponse(level="Success",serviceAffected = "UserInfoService",message = "UserId retrieved"))
        return userRepository.findUserById(id)


    }
    fun updateUserDetails(id: Long,updateUserRequest: UpdateUserRequest): ResponseEntity<UpdateUserDetailsResponse> {
        if(!userRepository.existsById(id)){
            logStream.sendToLogConsole(LogStreamResponse(level = "WARN",serviceAffected = "UserInfoService",message = "No UserId - $id"))
            return ResponseEntity.ok().body(UpdateUserDetailsResponse(message = "ID - $id Not Found",httpStatus = HttpStatus.BAD_REQUEST))

        }
       val fetchDetails= userRepository.findUserById(id)
        fetchDetails.email=updateUserRequest.email
        fetchDetails.username=updateUserRequest.username
        //SAVE CHANGES
        logStream.sendToLogConsole(LogStreamResponse(level ="SUCCESS",serviceAffected = "UserInfoService",message = "User with Id - ${fetchDetails.id} has updated details" ))
        userRepository.save(fetchDetails)
        return ResponseEntity.ok().body(UpdateUserDetailsResponse(message = "${updateUserRequest.email} details have been updated successfully",httpStatus = HttpStatus.OK))



    }
    fun blockUser(email:String):ResponseEntity<BlockResponse>{
        val userDetails=userRepository.findByEmail(email)
        //Map Userdetails
        userDetails.isEnabled=false
        // send log message
        logStream.sendToLogConsole(LogStreamResponse(level = "SUCCESS",serviceAffected = "UserInfoService",message = "${userDetails.email} has been Blocked"))
        //Save current Details
        userRepository.save(userDetails)
        return ResponseEntity.ok().body(BlockResponse(message = "${userDetails.email} has been blocked",httpStatus = HttpStatus.OK))


    }
    fun makeUserAdmin(id: Long): ResponseEntity<MakeAdminResponse> {
        val userDetails=userRepository.findUserById(id)
        if (userDetails.roles==UserRoles.ROLE_ADMIN){
            return ResponseEntity.badRequest().body(MakeAdminResponse(message = "The User Is Already An Admin",httpStatus = HttpStatus.BAD_REQUEST))
        }
        userDetails.roles= UserRoles.ROLE_ADMIN

        logStream.sendToLogConsole(LogStreamResponse(level = "SUCCESS",serviceAffected = "UserInfoService",message = "${userDetails.email} Has Been Promoted To Admin"))
        userRepository.save(userDetails)
        return ResponseEntity.ok().body(MakeAdminResponse(message = "${userDetails.email} Has Been Made An Admin",httpStatus = HttpStatus.OK))
    }
    fun removeUnverifiedUser(id: Long){
        userRepository.deleteById(id)
    }
}
