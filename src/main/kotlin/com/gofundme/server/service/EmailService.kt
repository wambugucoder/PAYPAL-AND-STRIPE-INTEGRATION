package com.gofundme.server.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service

@Service
class EmailService {

    @Autowired
    lateinit var javaMailSender: JavaMailSender

    fun sendAccountActivationEmail(token:String?){

    }
}

