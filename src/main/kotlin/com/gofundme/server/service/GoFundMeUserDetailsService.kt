package com.gofundme.server.service

import com.gofundme.server.repository.UserRepository
import com.gofundme.server.security.GoFundMeUserDetails
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class GoFundMeUserDetailsService:UserDetailsService {
    @Autowired
    lateinit var userRepository: UserRepository


    override fun loadUserByUsername(email:String): UserDetails {
        if (userRepository.existsByEmail(email)){
            val newUserDetails=userRepository.findByEmail(email)
            return GoFundMeUserDetails(newUserDetails)

        }
        else{
            throw UsernameNotFoundException("Invalid Email")
        }
    }

}