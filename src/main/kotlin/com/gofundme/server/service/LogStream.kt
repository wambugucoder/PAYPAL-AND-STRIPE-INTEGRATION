package com.gofundme.server.service

import com.gofundme.server.responseHandler.LogStreamResponse
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import org.springframework.stereotype.Service
import org.springframework.util.concurrent.ListenableFuture

@Service
class LogStream {

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var kafkaTemplate: KafkaTemplate<String,String>

    @Value("\${real.time.logs.topic}")
    lateinit var topic:String

   // SEND LOGS TO OUR VERY OWN REAL-TIME-LOGS SERVER
    fun sendToLogConsole(logStreamResponse: LogStreamResponse): ListenableFuture<SendResult<String, String>> {
        val stringLogResponse = objectMapper.writeValueAsString(logStreamResponse)
        return kafkaTemplate.send(topic,stringLogResponse)


    }
}