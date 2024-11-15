package org.example.exportkotlin.dto.manager.resp

import org.example.exportkotlin.domain.user.User

data class ManagerFindAllRespDto(
    val managerId: Long,
    val userId: Long,
    val email: String
) {
    companion object {
        fun of(managerId: Long, user: User): ManagerFindAllRespDto {
            return ManagerFindAllRespDto(
                managerId = managerId,
                userId = user.id!!,
                email = user.email
            )
        }
    }
}