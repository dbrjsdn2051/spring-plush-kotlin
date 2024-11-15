package org.example.exportkotlin.exception

class CustomApiException(private val errorCode: ErrorCode) : RuntimeException(errorCode.message)