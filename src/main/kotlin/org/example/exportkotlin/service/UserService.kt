package org.example.exportkotlin.service

import org.example.exportkotlin.domain.user.User
import org.example.exportkotlin.domain.user.UserRepository
import org.example.exportkotlin.dto.user.req.UserChangePasswordReqDto
import org.example.exportkotlin.dto.user.req.UserCreateRepDto
import org.example.exportkotlin.dto.user.resp.UserCreateRespDto
import org.example.exportkotlin.dto.user.resp.UserFindOneRespDto
import org.example.exportkotlin.exception.CustomApiException
import org.example.exportkotlin.exception.ErrorCode
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) {
    fun addUser(dto: UserCreateRepDto): UserCreateRespDto {
        if (userRepository.existsUserByEmail(dto.email)) {
            throw CustomApiException(ErrorCode.EXISTS_EMAIL)
        }

        val encodedPassword = passwordEncoder.encode(dto.password)
        return userRepository.save(User(dto.email, encodedPassword, dto.userRole, dto.nickname))
            .let { UserCreateRespDto.of(it) }
    }

    fun getUser(userId: Long): UserFindOneRespDto {
        return userRepository.findByIdOrNull(userId)
            ?.let { UserFindOneRespDto.of(it) }
            ?: throw CustomApiException(ErrorCode.USER_NOT_FOUND)
    }

    @Transactional
    fun changePassword(userId: Long, dto: UserChangePasswordReqDto) {
        val user = userRepository.findByIdOrNull(userId)
            ?: throw CustomApiException(ErrorCode.USER_NOT_FOUND)

        if (passwordEncoder.matches(dto.newPassword, user.password)) {
            throw CustomApiException(ErrorCode.VALID_ENCODE_PASSWORD_EQUAL)
        }

        if (!passwordEncoder.matches(dto.oldPassword, user.password)) {
            throw CustomApiException(ErrorCode.PASSWORD_MISSMATCH)
        }

        user.changePassword(passwordEncoder.encode(dto.newPassword))
    }
}