package com.gofundme.server.configs

import LogStreamResponse
import com.gofundme.server.service.DonationsService
import com.gofundme.server.service.LogStream
import com.gofundme.server.service.SchedulingService
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
    lateinit var schedulingService: SchedulingService

    @Async
    // every 10 minutes send approval emails in bulk
    @Scheduled(cron = "0 0/10 * * * * ?",zone = "Africa/Nairobi")
    fun sendApprovalEmails(){
        val donations=donationsService.getAllDonations()
        for (detail in donations ){
            if(!detail.approvalEmailSent){
                //send email

                //update to sent
            }
        }



    }

}

