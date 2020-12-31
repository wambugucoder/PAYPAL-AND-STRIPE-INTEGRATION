package com.gofundme.server.controller

import com.gofundme.server.requestHandler.RegisterHandler
import com.gofundme.server.responseHandler.RegisterResponse
import com.gofundme.server.service.AuthenticationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid


@RestController
class AuthenticationController {

    @Autowired
    lateinit var authenticationService: AuthenticationService

    @PostMapping("/api/v1/auth/register",produces = [MediaType.APPLICATION_JSON_VALUE],consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun registerUser(@RequestBody @Valid registerHandler: RegisterHandler,bindingResult: BindingResult): ResponseEntity<RegisterResponse> {
        if (bindingResult.hasErrors()){
            return ResponseEntity.badRequest().body(RegisterResponse(message = "Please Check your Details Again",httpStatus = HttpStatus.BAD_REQUEST))
        }
        return authenticationService.registerUser(registerHandler)


    }
}