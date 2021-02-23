package com.gofundme.server.configs

import LogStreamResponse
import com.gofundme.server.model.DonationsModel
import com.gofundme.server.service.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
@EnableAsync
class ScheduledTasksConfig {

    @Autowired
    lateinit var logStream: LogStream

    @Autowired
    lateinit var donationsService: DonationsService

    @Autowired
    lateinit var emailService: EmailService

    @Autowired
    lateinit var userInfoService: UserInfoService

    @Async
    // every 10 minutes send approval emails in bulk
    @Scheduled(cron = "0 0/3 * * * *",zone = "Africa/Nairobi")
    fun sendApprovalEmails(){
        val donations=donationsService.getAllDonations()
        if (donations.isNotEmpty()){
            for (detail in donations ){
                if(!detail.approvalEmailSent){
                    //send email
                    val donationDetails=DonationsModel(detail.details,detail.category,detail.target,detail.createdBy,detail.file)
                    emailService.sendAprrovalEmail(donationDetails)
                    //update to sent
                    donationsService.notifyEmailHasBeenSent(detail.id)
                }
            }
            //log it to streaming server
            logStream.sendToLogConsole(LogStreamResponse(level = "SUCCESS",serviceAffected = "SchedulingTasksConfig",message = "Batch Processing for Approval Emails Done"))

        }

    }
    @Async
    //remove unverified users every midnight.
    @Scheduled(cron = "0 0/3 * * * * ",zone = "Africa/Nairobi")
    fun removeUnverifiedUsers(){
        val allUserDetails=userInfoService.getAllUsers()
        if (allUserDetails.isNotEmpty()){
            for (user in allUserDetails){
                if (!user.isEnabled){
                    userInfoService.removeUnverifiedUser(user.id)
                }

            }
            //log it to streaming server
            logStream.sendToLogConsole(LogStreamResponse(level = "SUCCESS",serviceAffected = "SchedulingTasksConfig",message = "Unverified Users have been removed in Bulk"))

        }

    }

}

