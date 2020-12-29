package com.gofundme.server.service

import com.gofundme.server.responseHandler.LogStreamResponse
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*

@Service
class JwtService {

    @Value("\${secret.key}")
    lateinit var securityKey:String

    @Autowired
    lateinit var logStream: LogStream

    fun createActivationToken(payload:Map<String,String>): String? {
        return Jwts.builder()
            .setClaims(payload)
            .setSubject("Email Verification")
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + 1000*60*60*10))
            .signWith(SignatureAlgorithm.HS256,securityKey)
            .compact()
    }
    fun generateActivationToken(email:String): String? {
        logStream.sendToLogConsole(LogStreamResponse(level = "INFO",serviceAffected = "JwtService",message = "Generating an account Activation Token  for $email"))
        val payload:Map<String,String> = hashMapOf("email" to email)
        return createActivationToken(payload)
    }
}