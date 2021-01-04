package com.gofundme.server.service

import LogStreamResponse
import com.gofundme.server.model.AddressModel
import com.gofundme.server.model.UserModel
import com.gofundme.server.repository.UserRepository
import com.gofundme.server.requestHandler.RegisterHandler
import com.gofundme.server.responseHandler.AccountActivationResponse
import com.gofundme.server.responseHandler.RegisterResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthenticationService {
    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var emailService: EmailService

    @Autowired
    lateinit var jwtService: JwtService

    @Autowired
    lateinit var logStream: LogStream

    @Autowired
    lateinit var bCryptPasswordEncoder: BCryptPasswordEncoder

    fun registerUser(registerHandler: RegisterHandler):ResponseEntity<RegisterResponse>{
        //CHECK IF EMAIL EXISTS

        if(userRepository.existsByEmail(registerHandler.email)){

            logStream.sendToLogConsole(LogStreamResponse(level = "WARN",serviceAffected = "UserService",message = "Someone Tried To Register with ${registerHandler.email}"))

            return ResponseEntity.badRequest().body(RegisterResponse("${registerHandler.email} already exists",httpStatus = HttpStatus.BAD_REQUEST))
        }
        //IF NOT ->CREATE INSTANCE
        // ENCRYPT PASSWORD
        val encryptedPassword=bCryptPasswordEncoder.encode(registerHandler.password)

        val addressDetails =AddressModel(country = registerHandler.country,city = registerHandler.city)
        val registrationDetails= UserModel(username = registerHandler.username, email = registerHandler.email,password = encryptedPassword,address = addressDetails)

       //SAVE TO DB

        userRepository.save(registrationDetails)

        //CREATE A TOKEN CONTAINING EMAIL
       val activationToken:String?= jwtService.generateActivationToken(registerHandler.email)

        //SEND EMAIL TO CONFIRM
        emailService.sendAccountActivationEmail(activationToken,registerHandler)

        // RETURN SUCCESS
        logStream.sendToLogConsole(LogStreamResponse(level = "SUCCESS",serviceAffected = "UserService",message = "${registerHandler.username} created an Account"))

        return ResponseEntity.ok(RegisterResponse(message = "You have been Registered Successfully,Check your Email to Activate Account",httpStatus = HttpStatus.OK))


    }
    fun activateUserAccount(token:String): ResponseEntity<AccountActivationResponse> {
        if(jwtService.isActivationTokenExpired(token)==true){
           val email=jwtService.extractEmail(token)
            if (!checkIfUserAccountIsEnabled(email)){
                //SEARCH FOR USER-DETAILS AND ACTIVATE ACCOUNT
                val userDetails=userRepository.findByEmail(email)
                userDetails.isEnabled=true
                userDetails.isAccountLocked=false
                //UPDATE DETAILS
                userRepository.save(userDetails)
                //LOG DATA
                logStream.sendToLogConsole(LogStreamResponse(level = "INFO",serviceAffected = "authenticationService",message = "Account for $email has Been Activated Successfully"))
                //RETURN 200
                return ResponseEntity.ok().body(AccountActivationResponse(message = "Your Account Has Been Activated Succesfully",httpStatus = HttpStatus.OK))
            }
            else{
                logStream.sendToLogConsole(LogStreamResponse(level = "ERROR",serviceAffected = "authenticationService",message = "$email tried to Activate the Account Again"))
                return ResponseEntity.badRequest().body(AccountActivationResponse(message="You Already Activated Your Account",httpStatus = HttpStatus.BAD_REQUEST))
            }

        }
        else{
            //RETURN 400
            logStream.sendToLogConsole(LogStreamResponse(level = "ERROR",serviceAffected = "authenticationService",message = "An expired token was used for Activation ->$token"))
            return ResponseEntity.badRequest().body(AccountActivationResponse(message="Your Token Expired,Please Retrieve A New One",httpStatus = HttpStatus.BAD_REQUEST))
        }
    }
    fun checkIfUserAccountIsEnabled(email:String): Boolean {
        val userDetails=userRepository.findByEmail(email)
        return userDetails.isEnabled

    }
}