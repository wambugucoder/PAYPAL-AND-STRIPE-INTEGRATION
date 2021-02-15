package com.gofundme.server.configs

import com.gofundme.server.responseHandler.FileResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.multipart.MaxUploadSizeExceededException
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler


@ControllerAdvice
class FileLimitConfig: ResponseEntityExceptionHandler() {


    @ExceptionHandler(MaxUploadSizeExceededException::class)
    fun handleMaxFileSize(exc:MaxUploadSizeExceededException): ResponseEntity<FileResponse> {
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(FileResponse(message = "File Too Large"))
    }
}