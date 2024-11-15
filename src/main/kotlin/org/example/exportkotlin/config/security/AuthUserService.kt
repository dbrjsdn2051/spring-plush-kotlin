package org.example.exportkotlin.config.security

import org.example.exportkotlin.domain.user.UserRepository
import org.example.exportkotlin.exception.CustomApiException
import org.example.exportkotlin.exception.ErrorCode
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class AuthUserService(
    private val userRepository: UserRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String?): UserDetails {
        return userRepository.findByEmail(username!!)
            ?.let { AuthUser(it) }
            ?: throw CustomApiException(ErrorCode.USER_NOT_FOUND)
    }
}