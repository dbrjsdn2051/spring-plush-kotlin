package org.example.exportkotlin.dto.user.resp

import org.example.exportkotlin.domain.user.User

data class UserFindOneRespDto(
    private val id: Long,
    private val email: String
) {
    companion object {
        fun of(user: User): UserFindOneRespDto {
            return UserFindOneRespDto(
                id = user.id!!,
                email = user.email
            )
        }
    }
}