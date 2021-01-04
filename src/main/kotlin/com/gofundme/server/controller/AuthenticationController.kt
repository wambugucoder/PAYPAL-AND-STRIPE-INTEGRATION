package com.gofundme.server.controller

import LogStreamResponse
import com.gofundme.server.requestHandler.RegisterHandler
import com.gofundme.server.responseHandler.AccountActivationResponse
import com.gofundme.server.responseHandler.RegisterResponse
import com.gofundme.server.service.AuthenticationService
import com.gofundme.server.service.LogStream
import io.swagger.annotations.ApiOperation
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
    @ApiOperation(
        notes = "Registers Users and sends them A link that contains a token to Help them activate their account",
        value = "Registers A User"
    )
    fun registerUser(@RequestBody @Valid registerHandler: RegisterHandler,bindingResult: BindingResult): ResponseEntity<RegisterResponse> {
        if (bindingResult.hasErrors()){
           logStream.sendToLogConsole(LogStreamResponse(level = "WARN",serviceAffected = "ValidationService",message = "${registerHandler.username } gave wrong credentials during Login"))
            return ResponseEntity.badRequest().body(RegisterResponse(message = "Please Check your Details Again",httpStatus = HttpStatus.BAD_REQUEST))
        }
        return authenticationService.registerUser(registerHandler)


    }



    @PutMapping("/api/v1/auth/activate/{token}",produces = [MediaType.APPLICATION_JSON_VALUE],consumes = [MediaType.APPLICATION_JSON_VALUE])
    @ApiOperation(
        value = "Activate A Users Account to prove they are Real Users",
        notes = "Link sent via email Should Make it Possible for a user to Activate Account to be able to enjoy other services offered"
    )
    fun activateUserAccount(@PathVariable token:String): ResponseEntity<AccountActivationResponse> {
        if (token.isNullOrEmpty()){
            logStream.sendToLogConsole(LogStreamResponse(level = "ERROR",serviceAffected = "authenticationController",message = "No Token was Provided for Account Activation"))
            return ResponseEntity.badRequest().body(AccountActivationResponse(message = "No Token Has been Provided",httpStatus = HttpStatus.BAD_REQUEST))
        }
        else{
        logStream.sendToLogConsole(LogStreamResponse(level = "INFO",serviceAffected = "authenticationController",message = "About to activate user with token $token"))
        return authenticationService.activateUserAccount(token)
            }

    }
}