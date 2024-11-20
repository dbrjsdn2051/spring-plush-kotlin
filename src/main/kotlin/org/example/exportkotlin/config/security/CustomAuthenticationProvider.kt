package org.example.exportkotlin.config.security

import org.example.exportkotlin.exception.CustomApiException
import org.example.exportkotlin.exception.ErrorCode
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class CustomAuthenticationProvider(
    private val passwordEncoder: PasswordEncoder,
    private val authUserService: AuthUserService
) : AuthenticationProvider {

    override fun authenticate(authentication: Authentication?): Authentication {
        val email = authentication!!.name
        val password = authentication.credentials.toString()

        val user = authUserService.loadUserByUsername(email)

        if (!passwordEncoder.matches(password, user.password)) {
            throw CustomApiException(ErrorCode.PASSWORD_MISSMATCH)
        }

        return UsernamePasswordAuthenticationToken(user, null, user.authorities)
    }

    override fun supports(authentication: Class<*>?): Boolean {
        return UsernamePasswordAuthenticationToken::class.java.isAssignableFrom(authentication!!)
    }
}