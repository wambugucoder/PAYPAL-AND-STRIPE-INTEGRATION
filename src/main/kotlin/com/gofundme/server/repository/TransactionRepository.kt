package com.gofundme.server.repository

import com.gofundme.server.model.TransactionsModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface  TransactionRepository:JpaRepository<TransactionsModel,Long> {

}