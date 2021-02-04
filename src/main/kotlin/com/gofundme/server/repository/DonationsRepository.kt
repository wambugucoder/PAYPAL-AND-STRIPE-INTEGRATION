package com.gofundme.server.repository

import com.gofundme.server.model.DonationsModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DonationsRepository: JpaRepository<DonationsModel,Long> {
    fun findSpecificDonationById(id:Long):DonationsModel

}