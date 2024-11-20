package org.example.exportkotlin.exception

class CustomApiException(
    val errorCode: ErrorCode
) : RuntimeException(errorCode.message)