package com.gofundme.server.service

import com.gofundme.server.model.DonationsModel
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
class SchedulingService {

    @Autowired
    lateinit var redisTemplate: RedisTemplate<String,Any>

    val  hashOperations=redisTemplate.opsForHash<String,Any>()



    fun saveDonationToEmailSent(donationsModel: DonationsModel){

        hashOperations.put("DONATION_EMAIL","${donationsModel.id}",donationsModel)

    }

    fun retrieveDonationsWhereEmailsWereSent(): MutableMap<String, Any> {
        return hashOperations.entries("DONATION_EMAIL")
    }

}

