package org.example.exportkotlin.exception

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalControllerExceptionHandler {

    @ExceptionHandler(CustomApiException::class)
    fun customApiExceptionHandler(ex: CustomApiException): ResponseEntity<String> {
        return ResponseEntity.status(ex.errorCode.status.toInt()).body(ex.errorCode.message.toString())
    }
}