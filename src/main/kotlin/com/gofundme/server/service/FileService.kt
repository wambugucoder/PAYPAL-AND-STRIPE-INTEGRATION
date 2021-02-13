package com.gofundme.server.service

import com.gofundme.server.repository.FileRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class FileService {

    @Autowired
    lateinit var fileRepository: FileRepository

    fun storeFile(file:MultipartFile){


    }
}