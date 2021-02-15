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
import org.springframework.web.multipart.MultipartFile
import java.util.*
import javax.validation.Valid

@RestController
class DonationsController {
    @Autowired
    lateinit var donationsService:DonationsService


    @Autowired
    lateinit var logStream: LogStream


    @PostMapping("/api/v1/users/{id}/create-donation",produces = [MediaType.APPLICATION_JSON_VALUE],consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun createDonation(@RequestParam("file") file:MultipartFile ,@RequestParam("details") details:String ,@RequestParam("category") category:String,@RequestParam("target") target:String , @PathVariable id:Long): ResponseEntity<DonationResponse> {
        val donationHandler=DonationHandler(details,category,target)
        return donationsService.createDonation(donationHandler,id,file)

    }
    //Get all donations made
    //{id} is used normally to reference user and get their details
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
    //update donation details to server database
    @PutMapping("/api/v1/users/{uid}/close-donation/{did}",produces = [MediaType.APPLICATION_JSON_VALUE],consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun closeDonation(@PathVariable uid:Long,@PathVariable did:Long): ResponseEntity<ClosingDonationResponse> {
        return donationsService.closeDonation(did)

    }
   //Deletes a Donation
    @DeleteMapping("/api/v1/users/{uid}/delete-poll/{did}",produces = [MediaType.APPLICATION_JSON_VALUE],consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun deleteDonation(@PathVariable uid:Long,@PathVariable did:Long): ResponseEntity<ClosingDonationResponse> {
        return donationsService.deleteDonationById(did)

    }

}