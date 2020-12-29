package com.gofundme.server.service

import com.gofundme.server.model.AddressModel
import com.gofundme.server.model.UserModel
import com.gofundme.server.repository.UserRepository
import com.gofundme.server.requestHandler.RegisterHandler
import com.gofundme.server.responseHandler.LogStreamResponse
import com.gofundme.server.responseHandler.RegisterResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class UserService {
    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var emailService: EmailService

    @Autowired
    lateinit var jwtService: JwtService

    @Autowired
    lateinit var logStream: LogStream

    fun registerUser(registerHandler: RegisterHandler):ResponseEntity<RegisterResponse>{
        //CHECK IF EMAIL EXISTS

        if(userRepository.existByEmail(registerHandler.email)){

            logStream.sendToLogConsole(LogStreamResponse(level = "WARN",serviceAffected = "UserService",message = "Someone Tried To Register with ${registerHandler.email}"))

            return ResponseEntity.badRequest().body(RegisterResponse("${registerHandler.email} already exists",httpStatus = HttpStatus.BAD_REQUEST))
        }
        //IF NOT ->CREATE INSTANCE

        val addressDetails =AddressModel(country = registerHandler.country,city = registerHandler.city)
        val registrationDetails= UserModel(username = registerHandler.username, email = registerHandler.email,password = registerHandler.password,address = addressDetails)

       //SAVE TO DB

        userRepository.save(registrationDetails)

        //CREATE A TOKEN CONTAINING EMAIL
       val activationToken:String?= jwtService.generateActivationToken(registerHandler.email)

        //SEND EMAIL TO CONFIRM
        emailService.sendAccountActivationEmail(activationToken)

        // RETURN SUCCESS
        logStream.sendToLogConsole(LogStreamResponse(level = "SUCCESS",serviceAffected = "UserService",message = "${registerHandler.username} created an Account"))

        return ResponseEntity.ok(RegisterResponse(message = "You have been Registered Successfully,Check your Email to Activate Account",httpStatus = HttpStatus.OK))


    }
}