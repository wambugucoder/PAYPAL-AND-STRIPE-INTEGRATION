package com.gofundme.server.repository

import com.gofundme.server.model.FileModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FileRepository:JpaRepository<FileModel,Long> {
}