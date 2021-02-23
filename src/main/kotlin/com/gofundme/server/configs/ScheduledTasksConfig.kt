package com.gofundme.server.configs

import LogStreamResponse
import com.gofundme.server.service.LogStream
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

    @Async
    // at 12:00 AM every day
    @Scheduled(cron = "0 0 0 * * *",zone = "Africa/Nairobi")
    fun sendApprovalEmails(){


    }

}

