package org.example.exportkotlin.dto.user.req

data class UserChangePasswordReqDto(
    val oldPassword: String,
    val newPassword: String,
) {
}