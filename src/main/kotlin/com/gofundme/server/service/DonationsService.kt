package com.gofundme.server.service


import LogStreamResponse
import com.gofundme.server.model.DonationsModel
import com.gofundme.server.model.FileModel
import com.gofundme.server.model.TransactionsModel
import com.gofundme.server.repository.DonationsRepository
import com.gofundme.server.repository.UserRepository
import com.gofundme.server.requestHandler.DonationHandler
import com.gofundme.server.responseHandler.ClosingDonationResponse
import com.gofundme.server.responseHandler.DonationResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Service
class DonationsService {

    @Autowired
    lateinit var donationsRepository: DonationsRepository

    @Autowired
    lateinit var logStream: LogStream

    @Autowired
    lateinit var userRepository: UserRepository


    fun getAllDonations(): List<DonationsModel> {
        logStream.sendToLogConsole(LogStreamResponse(level = "SUCCESS",serviceAffected = "DonationsService",message = "All Donations Created have been Retrieved"))
        return donationsRepository.findAll()
    }

    fun getSpecificDonationsById(id:Long): Optional<DonationsModel> {
      if(!donationsRepository.existsById(id)){
          logStream.sendToLogConsole(LogStreamResponse(level = "ERROR",serviceAffected = "DonationsService",message = "Donation with id - $id does not exist"))
      }
        return donationsRepository.findById(id)
    }

    fun closeDonation(id:Long): ResponseEntity<ClosingDonationResponse> {
        if(!donationsRepository.existsById(id)){
            logStream.sendToLogConsole(LogStreamResponse(level = "ERROR",serviceAffected = "DonationsService",message = "Donation with id - $id does not exist"))
            return ResponseEntity.badRequest().body(ClosingDonationResponse(message = "Donation with invalid id - $id could not be closed",httpStatus = HttpStatus.BAD_REQUEST))

        }
        val donationDetails=donationsRepository.findSpecificDonationById(id)
        donationDetails.isOpen=false
        //SAVE
        donationsRepository.save(donationDetails)
        return ResponseEntity.ok().body(ClosingDonationResponse(message = "Donation with id - $id has been closed Successfully",httpStatus = HttpStatus.OK))

    }
    fun deleteDonationById(id:Long): ResponseEntity<ClosingDonationResponse> {
        if(!donationsRepository.existsById(id)){
            logStream.sendToLogConsole(LogStreamResponse(level = "ERROR",serviceAffected = "DonationsService",message = "Deleting Donation with id - $id does not exist"))
            return ResponseEntity.badRequest().body(ClosingDonationResponse(message = "Donation with invalid id - $id could not be deleted",httpStatus = HttpStatus.BAD_REQUEST))

        }
        donationsRepository.deleteById(id)
        return ResponseEntity.ok().body(ClosingDonationResponse(message = "Donation with id - $id has been deleted Successfully",httpStatus = HttpStatus.OK))
    }
    fun createDonation(donationHandler: DonationHandler,id:Long,fileReceived:MultipartFile): ResponseEntity<DonationResponse> {
        val userdetails= userRepository.findUserById(id)
        val fileName = fileReceived.originalFilename?.let { StringUtils.cleanPath(it) }
        val fileDetails=FileModel(fileName, fileReceived.contentType, fileReceived.bytes)
        val donationDetails  = DonationsModel(
            details = donationHandler.details,
            category = donationHandler.category,
            target = donationHandler.target ,
            createdBy = userdetails,
            file = fileDetails
        )
        donationsRepository.save(donationDetails)
        logStream.sendToLogConsole(LogStreamResponse(level = "INFO",serviceAffected = "DonationsService",message = "${userdetails.email} has created a new donation"))
        return ResponseEntity.ok().body(DonationResponse(message = "Donation Created Successfully",httpStatus = HttpStatus.OK))

    }
    fun updateDonationCountAndList(did:Long,transactionDetails:TransactionsModel){
        val getDetails=donationsRepository.getOne(did)
        //UPDATE MONEY
        getDetails.moneyDonated= getDetails.moneyDonated + transactionDetails.amountDonated.toInt()
        //UPDATE DONOR LIST
        getDetails.donors.add(transactionDetails.initiator.id.toString())
        //SAVE UPDATED DONATION LIST
        donationsRepository.save(getDetails)




    }
}
