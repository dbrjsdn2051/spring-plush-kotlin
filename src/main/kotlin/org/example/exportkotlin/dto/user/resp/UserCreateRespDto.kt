package org.example.exportkotlin.dto.user.resp

import org.example.exportkotlin.domain.user.User


data class UserCreateRespDto(
    val id: Long
) {
    companion object {
        fun of(user: User): UserCreateRespDto {
            return UserCreateRespDto(user.id!!)
        }
    }
}