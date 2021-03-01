package com.gofundme.server.controller

import LogStreamResponse
import com.gofundme.server.requestHandler.LoginHandler
import com.gofundme.server.requestHandler.RegisterHandler
import com.gofundme.server.responseHandler.AccountActivationResponse
import com.gofundme.server.responseHandler.LoginResponse
import com.gofundme.server.responseHandler.RegisterResponse
import com.gofundme.server.service.AuthenticationService
import com.gofundme.server.service.LogStream
import com.gofundme.server.service.StripeService
import com.stripe.model.Charge
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import javax.validation.Valid


@RestController
class AuthenticationController {

    @Autowired
    lateinit var authenticationService: AuthenticationService

    @Autowired
    lateinit var logStream: LogStream


    @PostMapping("/api/v1/auth/register",produces = [MediaType.APPLICATION_JSON_VALUE],consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun registerUser(@RequestBody @Valid registerHandler: RegisterHandler,bindingResult: BindingResult): ResponseEntity<RegisterResponse> {
        if (bindingResult.hasErrors()){
           logStream.sendToLogConsole(LogStreamResponse(level = "WARN",serviceAffected = "ValidationService",message = "${registerHandler.username } gave wrong credentials during Login"))
            return ResponseEntity.badRequest().body(RegisterResponse(message = "Please Check your Details Again",httpStatus = HttpStatus.BAD_REQUEST))
        }
        return authenticationService.registerUser(registerHandler)


    }



    @PutMapping("/api/v1/auth/activate/{token}",produces = [MediaType.APPLICATION_JSON_VALUE],consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun activateUserAccount(@PathVariable token:String): ResponseEntity<AccountActivationResponse> {
        return if (token.isNullOrEmpty()){
            logStream.sendToLogConsole(LogStreamResponse(level = "ERROR",serviceAffected = "authenticationController",message = "No Token was Provided for Account Activation"))
            ResponseEntity.badRequest().body(AccountActivationResponse(message = "No Token Has been Provided",httpStatus = HttpStatus.BAD_REQUEST))
        } else{
            logStream.sendToLogConsole(LogStreamResponse(level = "INFO",serviceAffected = "authenticationController",message = "About to activate user with token $token"))
            authenticationService.activateUserAccount(token)
        }

    }

    @PostMapping("/api/v1/auth/login",produces = [MediaType.APPLICATION_JSON_VALUE],consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun loginUser(@RequestBody @Valid loginHandler: LoginHandler,bindingResult: BindingResult): ResponseEntity<LoginResponse> {
        if (bindingResult.hasErrors()){
            logStream.sendToLogConsole(LogStreamResponse(level = "ERROR",serviceAffected = "authenticationController",message = "Validation Errors Encountered During Login by ${loginHandler.email}"))
            return ResponseEntity.badRequest().body(LoginResponse(message = "Please check your Details",httpStatus = HttpStatus.BAD_REQUEST,token=null))

        }
        return authenticationService.loginUser(loginHandler)

    }

}