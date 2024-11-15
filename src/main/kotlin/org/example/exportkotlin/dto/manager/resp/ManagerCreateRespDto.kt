package org.example.exportkotlin.dto.manager.resp

import org.example.exportkotlin.domain.user.User
import org.example.exportkotlin.dto.manager.req.ManagerCreateReqDto

data class ManagerCreateRespDto(
    val managerId: Long,
    val userId: Long,
    val email: String
) {
    companion object {
        fun of(managerId: Long, user: User): ManagerCreateRespDto {
            return ManagerCreateRespDto(
                managerId = managerId,
                userId = user.id!!,
                email = user.email
            )
        }
    }
}