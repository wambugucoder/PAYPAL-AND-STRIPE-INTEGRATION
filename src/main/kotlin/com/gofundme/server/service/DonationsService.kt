package com.gofundme.server.service


import LogStreamResponse
import com.gofundme.server.model.DonationsModel
import com.gofundme.server.repository.DonationsRepository
import com.gofundme.server.responseHandler.ClosingDonationResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.util.*

@Service
class DonationsService {

    @Autowired
    lateinit var donationsRepository: DonationsRepository

    @Autowired
    lateinit var logStream: LogStream


    fun getAllDonations(): List<DonationsModel> {
        logStream.sendToLogConsole(LogStreamResponse(level = "SUCCESS",serviceAffected = "DonationsService",message = "All Donations Created have been Retrieved"))
        return donationsRepository.findAllOrderBycreatedDateDesc()
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
}
