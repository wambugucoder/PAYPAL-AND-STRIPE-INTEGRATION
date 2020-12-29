package com.gofundme.server.repository

import com.gofundme.server.model.UserModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository:JpaRepository<UserModel,Long> {

    fun existByEmail(email:String):Boolean
}