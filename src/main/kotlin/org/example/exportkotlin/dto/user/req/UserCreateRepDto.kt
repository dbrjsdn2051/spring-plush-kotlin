package org.example.exportkotlin.dto.user.req

import org.example.exportkotlin.domain.user.UserRole

data class UserCreateRepDto(
    val email: String,
    val password: String,
    val userRole: UserRole,
    val nickname: String,
)