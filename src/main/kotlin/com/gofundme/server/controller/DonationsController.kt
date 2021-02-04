package com.gofundme.server.controller

import LogStreamResponse
import com.gofundme.server.model.DonationsModel
import com.gofundme.server.requestHandler.DonationHandler
import com.gofundme.server.responseHandler.ClosingDonationResponse
import com.gofundme.server.responseHandler.DonationResponse
import com.gofundme.server.service.DonationsService
import com.gofundme.server.service.LogStream
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.validation.Valid

@RestController
class DonationsController {
    @Autowired
    lateinit var donationsService:DonationsService


    @Autowired
    lateinit var logStream: LogStream


    @PostMapping("/api/v1/users/{id}/create-donation",produces = [MediaType.APPLICATION_JSON_VALUE],consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createDonation(@RequestBody @Valid donationHandler: DonationHandler,bindingResult: BindingResult, @PathVariable id:Long): ResponseEntity<DonationResponse> {
        if (bindingResult.hasErrors()){
            logStream.sendToLogConsole(LogStreamResponse(level = "WARN",serviceAffected = "DonationController",message = "User with id - $id has errors in their donation Request-Body"))
            return ResponseEntity.badRequest().body(DonationResponse(message = "Please Check Your Details Again",httpStatus = HttpStatus.BAD_REQUEST))

        }
        return donationsService.createDonation(donationHandler,id)

    }
    @GetMapping("/api/v1/users/{id}/all-donations",produces = [MediaType.APPLICATION_JSON_VALUE],consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun getAllDonations(@PathVariable id:Long): List<DonationsModel> {
        logStream.sendToLogConsole(LogStreamResponse(level="INFO",serviceAffected = "DonationController",message = "User ith ID- $id has request for all donations"))
        return donationsService.getAllDonations()
    }

    @GetMapping("/api/v1/users/{uid}/donations/{did}",produces = [MediaType.APPLICATION_JSON_VALUE],consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun getSpecificDonation(@PathVariable uid: Long, @PathVariable did: Long): Optional<DonationsModel> {
        logStream.sendToLogConsole(LogStreamResponse(level = "INFO",serviceAffected = "DonationController",message = "User with ID -$uid is accessing a donation with ID -$did"))
        return donationsService.getSpecificDonationsById(did)

    }
    @PutMapping("/api/v1/users/{uid}/close-poll/{did}",produces = [MediaType.APPLICATION_JSON_VALUE],consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun closeDonation(@PathVariable uid:Long,@PathVariable did:Long): ResponseEntity<ClosingDonationResponse> {
        return donationsService.closeDonation(did)

    }

    @DeleteMapping("/api/v1/users/{uid}/delete-poll/{did}",produces = [MediaType.APPLICATION_JSON_VALUE],consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun deleteDonation(@PathVariable uid:Long,@PathVariable did:Long): ResponseEntity<ClosingDonationResponse> {
        return donationsService.deleteDonationById(did)

    }

}