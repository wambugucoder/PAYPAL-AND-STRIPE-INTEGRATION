package com.gofundme.server.service

import LogStreamResponse
import com.gofundme.server.model.UserModel
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*
import java.util.function.Function

@Service
class JwtService {

    @Value("\${secret.key}")
    lateinit var securityKey:String

    @Autowired
    lateinit var logStream: LogStream

    fun createActivationToken(payload:Map<String,String>,email:String): String? {
        return Jwts.builder()
            .setClaims(payload)
            .setSubject(email)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + 1000*60*60*10))
            .signWith(SignatureAlgorithm.HS256,securityKey)
            .compact()
    }
    fun generateActivationToken(email:String): String? {
        logStream.sendToLogConsole(LogStreamResponse(level = "INFO",serviceAffected = "JwtService",message = "Generating an account Activation Token  for $email"))
        val payload:Map<String,String> = hashMapOf("email" to email)
        return createActivationToken(payload,email)
    }
   fun extractExpiryDate(token:String): Date? {
       logStream.sendToLogConsole(LogStreamResponse(level = "INFO",serviceAffected = "jwtService",message = "Extracting Expiry Date for $token"))
       return extractSpecificClaim(token,Claims::getExpiration)

   }
    fun extractEmail(token:String):String{
        logStream.sendToLogConsole(LogStreamResponse(level = "INFO",serviceAffected = "jwtService",message = "Extracting Email  for $token"))
        return extractSpecificClaim(token,Claims::getSubject)
    }
    fun isActivationTokenExpired(token:String): Boolean? {
        logStream.sendToLogConsole(LogStreamResponse(level = "INFO",serviceAffected = "jwtService",message = "Validating Token-> $token"))
        return extractExpiryDate(token)?.after(Date())

    }
    fun <T> extractSpecificClaim(token:String,claimsResolver:Function<Claims,T>): T {
        val claim= extractAllClaims(token)
        return claimsResolver.apply(claim)
    }
    fun extractAllClaims(token: String): Claims{
        return Jwts.parser().setSigningKey(securityKey).parseClaimsJws(token).body

    }
    fun generateLoginToken(user:UserModel): String? {
        val payload:Map<String,String> = hashMapOf(
            "email" to user.email,
            "role" to user.roles.toString(),
            "createdAt" to user.createdDate.toString(),
            "verified" to user.isEnabled.toString()
        )
        return createActivationToken(payload,user.email)
    }
}