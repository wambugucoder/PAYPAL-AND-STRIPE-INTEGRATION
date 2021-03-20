package com.gofundme.server.security

import com.gofundme.server.responseHandler.LogStreamResponse
import com.gofundme.server.service.GoFundMeUserDetailsService
import com.gofundme.server.service.JwtService
import com.gofundme.server.service.LogStream
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Component
class JwtFilter: OncePerRequestFilter() {

    @Autowired
    lateinit var logStream: LogStream

    @Autowired
    lateinit var jwtService:JwtService

    @Autowired
    lateinit var goFundMeUserDetailsService: GoFundMeUserDetailsService

    private var jwtToken: String? =null
    private var email: String? =null

    //SECURITY FEATURE TO CAPTURE TOKEN,VALIDATE TOKEN ,GET THEIR DETAILS,AUTHORIZE THEM
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        // GET AUTHORIZATION HEADER FROM REQUEST
         val authHeader=request.getHeader("Authorization")

        if (authHeader!=null && authHeader.startsWith("Bearer ")){
            jwtToken=authHeader.substring(7)
            email= jwtService.extractEmail(jwtToken!!)
           logStream.sendToLogConsole(LogStreamResponse(level="INFO",serviceAffected="jwtFilter",message="$email is preparing to be authorized"))
        }
        //VALIDATE TOKEN
        if(email !=null && SecurityContextHolder.getContext().authentication==null){
           // FETCH USER DETAILS
            val confirmationDetails=goFundMeUserDetailsService.loadUserByUsername(email!!)
            if (jwtService.isvalidatedAuthHeaderToken(jwtToken!!,confirmationDetails)){
                val passDetailsToAuthorizationServer=UsernamePasswordAuthenticationToken(confirmationDetails,null,confirmationDetails.authorities)
                passDetailsToAuthorizationServer.details= WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication=passDetailsToAuthorizationServer
                //SEND MESSAGE TO LOG
                logStream.sendToLogConsole(LogStreamResponse(level="INFO",serviceAffected="jwtFilter",message="$email has been authorized to access API"))
            }

        }
        logStream.sendToLogConsole(LogStreamResponse(level="WARN",serviceAffected="jwtFilter",message="An UnAuthorized API has just passed through->Might contain permissions or Not"))
        filterChain.doFilter(request, response)



    }

}