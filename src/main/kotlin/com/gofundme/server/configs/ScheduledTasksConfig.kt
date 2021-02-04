package com.gofundme.server.configs

import LogStreamResponse
import com.gofundme.server.service.LogStream
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class ScheduledTasksConfig {

    @Autowired
    lateinit var logStream: LogStream

    @Scheduled(
        fixedRate = 2000
    )
    fun scheduleActivity() {
       logStream.sendToLogConsole(LogStreamResponse(level = "INFO",serviceAffected = "ScheduleActivity",message = "Hello-World"))
    }
}